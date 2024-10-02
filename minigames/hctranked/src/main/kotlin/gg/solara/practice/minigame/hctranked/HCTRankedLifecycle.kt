package gg.solara.practice.minigame.hctranked

import com.cryptomorin.xseries.XMaterial
import gg.scala.lemon.hotbar.HotbarPreset
import gg.scala.lemon.hotbar.HotbarPresetHandler
import gg.scala.lemon.hotbar.entry.impl.DynamicHotbarPresetEntry
import gg.solara.practice.minigame.hctranked.events.GameEndGameEvent
import gg.solara.practice.minigame.hctranked.events.GameEvent
import gg.solara.practice.minigame.hctranked.menu.PvPClassSelectionMenu
import gg.solara.practice.minigame.hctranked.shared.HCTRankedGameConfiguration
import gg.solara.practice.minigame.hctranked.shared.HCTRankedMode
import gg.solara.practice.minigame.hctranked.shared.profile.HCTRankedProfileService
import gg.tropic.practice.games.GameReport
import gg.tropic.practice.games.GameState
import gg.tropic.practice.games.event.GameStartEvent
import gg.tropic.practice.games.event.PlayerJoinGameEvent
import gg.tropic.practice.map.metadata.impl.MapChestMetadata
import gg.tropic.practice.map.metadata.scanner.impl.MapChestMetadataScanner
import gg.tropic.practice.minigame.AbstractMiniGameGameImpl
import gg.tropic.practice.minigame.MiniGameLifecycle
import gg.tropic.practice.minigame.MiniGameScoreboard
import me.lucko.helper.Events
import me.lucko.helper.Helper
import me.lucko.helper.Schedulers
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.material.Chest
import java.util.*

/**
 * @author Topu
 * @date 10/2/2024
 */
class HCTRankedLifecycle(
    override val configuration: HCTRankedGameConfiguration,
    override val game: AbstractMiniGameGameImpl<HCTRankedGameConfiguration>
) : MiniGameLifecycle<HCTRankedGameConfiguration>, CompositeTerminable by CompositeTerminable.create()
{

    val events = LinkedList<GameEvent>().apply {
        this += GameEndGameEvent(this@HCTRankedLifecycle)
    }

    var eventTerminable: CompositeTerminable = CompositeTerminable.create()

    override val scoreboard: MiniGameScoreboard = HCTRankedScoreboard(game)
    override fun configure()
    {
        val audiences = BukkitAudiences.create(Helper.hostPlugin())
        val world = game.arenaWorld
            ?: return

        Schedulers
            .sync()
            .run {
                game.map.findSpawnLocations()
                    .forEach {
                        it.position.toLocation(world).block
                            .getRelative(BlockFace.DOWN)
                            .setType(Material.AIR, false)
                    }
            }

        val hotbarPreset = HotbarPreset()
        hotbarPreset.addSlot(0, DynamicHotbarPresetEntry().apply {
            onBuild = {
                ItemBuilder.of(XMaterial.LIME_DYE)
                    .name("${CC.GREEN}Prepared ${CC.GRAY}(Right Click)")
                    .build()
            }

            onClick = context@{
                it.performCommand("ready")
            }
        })

        if (configuration.mode != HCTRankedMode.ALL_DIAMOND)
        {
            hotbarPreset.addSlot(1, DynamicHotbarPresetEntry().apply {
                onBuild = {
                    ItemBuilder.of(XMaterial.LIME_DYE)
                        .name("${CC.YELLOW}PvP Class Selector ${CC.GRAY}(Right Click)")
                        .build()
                }

                onClick = context@{
                    PvPClassSelectionMenu().openMenu(it)
                }
            })
        }

        hotbarPreset.addSlot(8, DynamicHotbarPresetEntry().apply {
            onBuild = {
                ItemBuilder.of(XMaterial.RED_DYE)
                    .name("${CC.RED}Leave Game ${CC.GRAY}(Right Click)")
                    .build()
            }

            onClick = context@{
                it.performCommand("leavegame")
            }
        })

        HotbarPresetHandler.startTrackingHotbar(
            game.expectation.toString(),
            hotbarPreset
        )

        val updateHotBarTerminable = CompositeTerminable.create()
        updateHotBarTerminable.bindWith(game)
        updateHotBarTerminable.with {
            game.toBukkitPlayers().filterNotNull()
                .forEach {
                    audiences.player(it)
                        .sendActionBar(Component.empty())
                }
        }

        Events
            .subscribe(PlayerJoinGameEvent::class.java)
            .filter { it.game.expectation == game.expectation }
            .filter { it.game.miniGameLifecycle is HCTRankedLifecycle }
            .filter { it.game.state(GameState.Waiting) || it.game.state(GameState.Starting) }
            .handler {
                hotbarPreset.applyToPlayer(it.player)
            }
            .bindWith(updateHotBarTerminable)

        Events
            .subscribe(GameStartEvent::class.java)
            .filter { it.game.expectation == game.expectation }
            .handler {
                HotbarPresetHandler.forget(game.expectation.toString())
                updateHotBarTerminable.closeAndReportException()

                events.first().start = System.currentTimeMillis()

                Schedulers
                    .sync()
                    .runRepeating({ task ->
                        if (!game.state(GameState.Playing))
                        {
                            task.closeAndReportException()
                            eventTerminable?.closeAndReportException()
                            return@runRepeating
                        }

                        val currentEvent = events.peekFirst()
                        if (currentEvent == null)
                        {
                            task.closeAndReportException()
                            eventTerminable?.closeAndReportException()
                            return@runRepeating
                        }

                        val estimatedExecution = currentEvent.estimatedExecution()
                        if (estimatedExecution <= System.currentTimeMillis())
                        {
                            eventTerminable.closeAndReportException()
                            currentEvent.run()

                            eventTerminable = CompositeTerminable.create()
                            eventTerminable.bindWith(this)

                            events.pop()
                            events.peekFirst()?.start = System.currentTimeMillis()
                        }
                    }, 0L, 20L)
                    .bindWith(this)
            }
            .bindWith(game)

        Schedulers.sync()
            .run {
                game.map.metadata.metadata
                    .filterIsInstance<MapChestMetadata>()
                    .forEach {
                        it.locations.values.flatten().forEach { position ->
                            val location = position.toLocation(world)
                            val face = MapChestMetadataScanner.inverseValuesMappings[position.yaw]
                                ?: BlockFace.EAST

                            val chest = Chest(face)
                            world.getBlockAt(location)
                                .apply {
                                    setType(Material.CHEST, true)
                                }

                            world.getBlockAt(location).state
                                .apply {
                                    data = chest
                                    update()
                                }
                        }
                    }
            }
    }

    inner class HCTRankedScoreboard(override val game: AbstractMiniGameGameImpl<HCTRankedGameConfiguration>) :
        MiniGameScoreboard
    {
        override fun titleFor(player: Player) = "${CC.B_PRI}HCTRANKED"

        fun Player.addSurrounding(board: MutableList<String>, surrounded: () -> List<String>)
        {
            board += surrounded()
            board += "      ${CC.GRAY}       ${CC.GRAY}"
            board += "${CC.GRAY}Players: ${CC.GREEN}${
                game.allNonSpectators().size
            }"
            board += ""
            board += "${CC.GRAY}Kills: ${CC.PRI}${HCTRankedProfileService.find(player)?.kills}}"
            board += ""
            board += "${CC.GRAY}Map: ${CC.GREEN}${game.map.displayName}"
        }

        override fun createWaitingScoreboardFor(player: Player, board: MutableList<String>)
        {
            board += "${CC.GRAY}Players: ${CC.PRI}${game.allNonSpectators().size}/4"
            board += ""
            board += "${CC.GRAY}Waiting for players..."
            board += ""
            board += "${CC.GRAY}Map: ${CC.GREEN}${game.map.displayName}"
        }

        override fun createStartingScoreboardFor(player: Player, board: MutableList<String>)
        {
        }

        override fun createInGameScoreboardFor(player: Player, board: MutableList<String>)
        {
        }

        override fun createEndingScoreboardFor(player: Player, report: GameReport?, board: MutableList<String>)
        {
            player.addSurrounding(board) {
                listOf(
                    "${CC.GREEN}Game Ended!"
                )
            }
        }
    }
}