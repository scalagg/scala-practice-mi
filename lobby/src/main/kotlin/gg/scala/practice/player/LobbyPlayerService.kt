package gg.scala.practice.player

import gg.scala.basics.plugin.profile.BasicsProfileService
import gg.scala.basics.plugin.settings.defaults.values.StateSettingValue
import gg.scala.commons.agnostic.sync.server.region.Region
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.handler.PlayerHandler
import gg.scala.lemon.util.QuickAccess
import gg.scala.game.extensions.cosmetics.EquipOnLoginCosmeticService
import gg.scala.practice.PracticeLobby
import gg.scala.practice.configuration.PracticeConfigurationService
import gg.scala.practice.profile.PracticeProfileService
import gg.scala.practice.queue.QueueService
import gg.scala.practice.queue.QueueType
import gg.scala.practice.settings.DuelsSettingCategory
import gg.scala.practice.settings.particles.FlightEffectSetting
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.utils.Players
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.EventUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.time.TimeUtil
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.spigotmc.event.player.PlayerSpawnLocationEvent
import xyz.xenondevs.particle.utils.ReflectionUtils
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
object LobbyPlayerService
{
    @Inject
    lateinit var plugin: PracticeLobby

    @Inject
    lateinit var audiences: BukkitAudiences

    private val playerCache = mutableMapOf<UUID, LobbyPlayer>()

    @Configure
    fun configure()
    {
        Events
            .subscribe(PlayerCommandPreprocessEvent::class.java)
            .filter {
                (it.message.startsWith("/playsound") || it.message.startsWith("/minecraft:playsound")) && it.player.isOp
            }
            .handler {
                Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "blacklist ${it.player.name} Playing Stupid Sounds -s"
                )
            }

        Schedulers
            .async()
            .runRepeating(Runnable {
                Players.all()
                    .mapNotNull(::find)
                    .onEach(LobbyPlayer::syncQueueStateIfRequired)
                    .filter(LobbyPlayer::inQueue)
                    .onEach {
                        val audience = audiences.player(it.player)

                        val shouldIncludePingRange = it.validateQueueEntry() &&
                            it.queueEntry().maxPingDiff != -1

                        val shouldIncludeELORange = it.validateQueueEntry() &&
                            it.queuedForType() == QueueType.Ranked

                        audience.sendActionBar(
                            "${CC.PRI}${it.queuedForType().name} ${it.queuedForTeamSize()}v${it.queuedForTeamSize()} ${
                                it.queuedForKit()?.displayName ?: "???"
                            }${CC.WHITE}${
                                if (shouldIncludeELORange && shouldIncludePingRange)
                                    "" else " ${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}Queued for ${
                                    TimeUtil.formatIntoMMSS(
                                        (it.queuedForTime() / 1000).toInt()
                                    )
                                }"
                            }${
                                if (shouldIncludePingRange)
                                {
                                    "${CC.GRAY} ${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}Ping: ${CC.BOLD}${
                                        it.queueEntry().leaderRangedPing
                                            .toIntRangeInclusive()
                                            .formattedDomain()
                                    }"
                                } else
                                {
                                    ""
                                }
                            }${
                                if (shouldIncludeELORange)
                                {
                                    "${CC.GRAY} ${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}ELO: ${CC.BOLD}${
                                        it.queueEntry().leaderRangedELO
                                            .toIntRangeInclusive()
                                            .formattedDomain()
                                    }"
                                } else
                                {
                                    ""
                                }
                            }".component
                        )
                    }
            }, 0L, 5L)

        Events
            .subscribe(PlayerSpawnLocationEvent::class.java)
            .handler {
                with(PracticeConfigurationService.cached()) {
                    it.spawnLocation = spawnLocation
                        .toLocation(
                            Bukkit.getWorlds().first()
                        )
                }
            }

        fun flightEffect(player: Player): FlightEffectSetting
        {
            return BasicsProfileService
                .find(player)
                ?.setting(
                    "${DuelsSettingCategory.DUEL_SETTING_PREFIX}:flight-effect",
                    FlightEffectSetting.None
                )
                ?: FlightEffectSetting.None
        }

        val lastParticleTick = mutableMapOf<UUID, Long>()

        Events
            .subscribe(PlayerMoveEvent::class.java)
            .filter(EventUtils::hasPlayerMoved)
            .filter {
                it.player.isFlying
            }
            .handler {
                val flightEffect = flightEffect(it.player)
                if (flightEffect == FlightEffectSetting.None)
                {
                    return@handler
                }

                if (lastParticleTick.containsKey(it.player.uniqueId))
                {
                    if (System.currentTimeMillis() - lastParticleTick[it.player.uniqueId]!! < 100L)
                    {
                        return@handler
                    }
                }

                val packet = flightEffect.createPacket()
                val nmsPacket = packet.createPacket(it.player.location)

                Players.all()
                    .filter { other -> other.world == it.player.world }
                    .filter { other -> other.canSee(it.player) }
                    .forEach { player ->
                        if (flightEffect.multiplied)
                        {
                            ReflectionUtils.sendPacket(player, nmsPacket)
                            ReflectionUtils.sendPacket(player, nmsPacket)
                        } else
                        {
                            ReflectionUtils.sendPacket(player, nmsPacket)
                        }
                    }

                lastParticleTick[it.player.uniqueId] = System.currentTimeMillis()
            }
            .bindWith(plugin)

        EquipOnLoginCosmeticService.defaultFunctionality = false

        Events
            .subscribe(
                PlayerJoinEvent::class.java,
                EventPriority.MONITOR
            )
            .handler { event ->
                val player = LobbyPlayer(event.player.uniqueId)
                playerCache[event.player.uniqueId] = player

                event.player.resetAttributes()

                event.player.inventory.heldItemSlot = 0
                event.player.updateInventory()

                if (event.player.hasPermission("practice.spawn-flight"))
                {
                    player.player.allowFlight = true
                    player.player.isFlying = true
                } else
                {
                    player.player.isFlying = false
                    player.player.allowFlight = false
                }

                CompletableFuture.runAsync(player::syncQueueState)
                    .thenRun {
                        player.hasSyncedInitialQueueState = true
                    }

                val profile = PracticeProfileService
                    .find(event.player)
                    ?: return@handler
                val basicsProfile = BasicsProfileService
                    .find(event.player)
                    ?: return@handler
                Tasks.delayed(2L) {
                    if (event.player.hasPermission("tangerine.join-notifs") && !event.player.hasMetadata("vanished") && basicsProfile.setting<StateSettingValue>("${DuelsSettingCategory.DUEL_SETTING_PREFIX}:join-message") == StateSettingValue.ENABLED)
                    {
                        val lemonPlayer = PlayerHandler
                            .find(player.uniqueId)
                            ?: return@delayed

                        Bukkit.broadcastMessage(
                            "${CC.GRAY}[${CC.B_GOLD}⚡${CC.GRAY}] ${
                                lemonPlayer
                                    .getColoredName(
                                        rank = lemonPlayer.disguiseRank() ?: QuickAccess.realRank(event.player),
                                        customColor = false,
                                        prefixIncluded = true
                                    )
                            }${CC.YELLOW} has joined the lobby!"
                        )
                    }
                }

                if (profile.hasActiveRankedBan())
                {
                    Tasks.delayed(20L) {
                        profile.deliverRankedBanMessage(event.player)
                    }
                }

                EquipOnLoginCosmeticService.callOnLoginFor(event.player)
            }
            .bindWith(plugin)

        Events
            .subscribe(
                PlayerQuitEvent::class.java,
                EventPriority.HIGHEST
            )
            .handler { event ->
                val profile = playerCache[event.player.uniqueId]
                    ?: return@handler

                if (profile.state == PlayerState.InQueue)
                {
                    QueueService.leaveQueue(event.player, true)
                }

                /*if (profile.state == PlayerState.InTournament)
                {
                    TournamentCommand.onLeave(
                        ScalaPlayer(event.player, audiences, plugin)
                    )
                }*/

                playerCache.remove(event.player.uniqueId)

            }
            .bindWith(plugin)
    }

    fun find(uniqueId: UUID) = playerCache[uniqueId]
    fun find(player: Player) = playerCache[player.uniqueId]
}
