package gg.solara.practice.minigame.skywars

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.solara.practice.minigame.skywars.chests.ChestRefillHologram
import gg.solara.practice.minigame.skywars.events.ChestRefillEvent
import gg.solara.practice.minigame.skywars.shared.SkyWarsGameConfiguration
import gg.solara.practice.minigame.skywars.shared.cages.*
import gg.tropic.practice.games.GameService
import gg.tropic.practice.games.GameState
import gg.tropic.practice.games.event.GameStartEvent
import gg.tropic.practice.games.event.PlayerSelectSpawnLocationEvent
import gg.tropic.practice.minigame.AbstractMiniGameGameImpl
import gg.tropic.practice.minigame.BasicMiniGameOrchestrator
import gg.tropic.practice.minigame.broadcastSeconds
import gg.tropic.practice.minigame.event.MiniGameStartTickEvent
import me.lucko.helper.Events
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.block.Chest
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.chest.ChestCloseSoundAction
import org.bukkit.event.block.chest.EntityChestCloseEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
@Service
object SkyWarsGameOrchestrator : BasicMiniGameOrchestrator<SkyWarsGameConfiguration>()
{
    override fun prepare(
        miniGame: AbstractMiniGameGameImpl<SkyWarsGameConfiguration>,
        configuration: SkyWarsGameConfiguration
    ) = SkyWarsLifecycle(configuration, miniGame)

    @Configure
    fun configure()
    {
        /**
         * Global subscriptions
         */
        Events
            .subscribe(MiniGameStartTickEvent::class.java)
            .filter { it.game.miniGameLifecycle is SkyWarsLifecycle }
            .filter { it.secondsLeftUntilStart in broadcastSeconds }
            .handler {
                it.game.playSound(Sound.NOTE_STICKS, 1.0f)
                it.game.sendMessage("${CC.YELLOW}Cages open in ${CC.RED}${it.secondsLeftUntilStart}${CC.YELLOW} second${
                    if (it.secondsLeftUntilStart == 1) "" else "s"
                }!")
            }

        Events
            .subscribe(PlayerSelectSpawnLocationEvent::class.java)
            .filter { it.game.miniGameLifecycle is SkyWarsLifecycle }
            .filter { !it.spectator }
            .filter { it.game.state(GameState.Waiting) || it.game.state(GameState.Starting) }
            .handler {
                val baseBlock = it.location.world
                    .getBlockAt(it.location)
                    .getRelative(BlockFace.DOWN)

                val randomCosmetic = CageCategory
                    .cosmetics()
                    .firstOrNull { cosmetic -> cosmetic.equipped(it.player) } as CageCosmeticLike?

                val randomCage = randomCosmetic
                    ?.cage
                    ?.linkedSchema
                    ?.let { schema ->
                        SchemaCageDataSync.cached().schemas[schema]
                    }
                    ?: SchemaCageDefaultSchemas.DEFAULT

                randomCage.applyTo(baseBlock)

                val skyWars = it.game.miniGameLifecycle as SkyWarsLifecycle
                skyWars.cages[it.player.uniqueId] = LocalCage(
                    cage = randomCage,
                    location = baseBlock.location.toVector(),
                    owner = it.player.uniqueId
                )
            }

        Events
            .subscribe(PlayerQuitEvent::class.java)
            .handler {
                val game = GameService.byPlayer(it.player)
                    ?: return@handler

                if (game.state(GameState.Waiting) || game.state(GameState.Playing))
                {
                    val skyWars = game.miniGameLifecycle as SkyWarsLifecycle
                    skyWars.cages[it.player.uniqueId]?.apply {
                        cage.removeAll(
                            location.toLocation(game.arenaWorld!!).block
                        )
                    }
                }
            }

        Events
            .subscribe(BlockBreakEvent::class.java)
            .handler {
                val game = GameService.byPlayer(it.player)
                    ?: return@handler

                val miniGameLifecycle = game.miniGameLifecycle
                if (miniGameLifecycle is SkyWarsLifecycle)
                {
                    if (!it.block.hasMetadata("hologram"))
                    {
                        return@handler
                    }

                    with(
                        it.block.getMetadata("hologram")
                            .first().value() as ChestRefillHologram
                    )
                    {
                        closeAndReportException()
                    }
                }
            }

        Events
            .subscribe(EntityChestCloseEvent::class.java)
            .handler {
                it.chestCloseSoundSetting = ChestCloseSoundAction.BROADCAST
                it.isShouldSendCloseAnimation = false
            }

        Events
            .subscribe(PlayerInteractEvent::class.java)
            .filter { it.clickedBlock != null && it.action == Action.RIGHT_CLICK_BLOCK && it.clickedBlock.state.data is org.bukkit.material.Chest }
            .handler {
                val game = GameService.byPlayer(it.player)
                    ?: return@handler

                if (it.player.hasMetadata("spectator"))
                {
                    it.isCancelled = true
                    return@handler
                }

                if (game.state(GameState.Waiting) || game.state(GameState.Starting))
                {
                    it.isCancelled = true
                    return@handler
                }

                val miniGameLifecycle = game.miniGameLifecycle
                if (miniGameLifecycle is SkyWarsLifecycle)
                {
                    val firstEvent = miniGameLifecycle.events.firstOrNull()
                    if (
                        firstEvent is ChestRefillEvent &&
                        !it.clickedBlock.hasMetadata("hologram")
                    )
                    {
                        with(
                            ChestRefillHologram(
                                miniGameLifecycle,
                                firstEvent,
                                it.clickedBlock.location
                                    .add(0.500, 0.0, 0.500)
                            )
                        ) {
                            create()
                            bindWith(firstEvent.terminable)
                        }
                    }
                }
            }

        Events
            .subscribe(GameStartEvent::class.java)
            .filter { it.game.miniGameLifecycle is SkyWarsLifecycle }
            .handler {
                it.game.playSound(Sound.ENDERDRAGON_GROWL, 1.0f)
                it.game.sendMessage("${CC.YELLOW}Cages opened! ${CC.RED}FIGHT!")
            }
    }

    override val id = "skywars"
    override val minimumPlayersRequiredToEnterStarting = 4
    override val startGameCountDown = 5
}
