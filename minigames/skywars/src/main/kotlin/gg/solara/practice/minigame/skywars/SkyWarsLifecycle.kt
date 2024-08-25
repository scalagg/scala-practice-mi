package gg.solara.practice.minigame.skywars

import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.lemon.util.SplitUtil
import gg.solara.practice.minigame.skywars.chests.ChestController
import gg.solara.practice.minigame.skywars.events.*
import gg.solara.practice.minigame.skywars.shared.SkyWarsGameConfiguration
import gg.solara.practice.minigame.skywars.shared.cages.*
import gg.tropic.game.extensions.cosmetics.CosmeticRegistry
import gg.tropic.practice.games.GameReport
import gg.tropic.practice.games.GameService
import gg.tropic.practice.games.GameState
import gg.tropic.practice.games.event.GameStartEvent
import gg.tropic.practice.games.event.PlayerSelectSpawnLocationEvent
import gg.tropic.practice.map.metadata.impl.MapChestMetadata
import gg.tropic.practice.map.metadata.scanner.impl.MapChestMetadataScanner
import gg.tropic.practice.minigame.AbstractMiniGameGameImpl
import gg.tropic.practice.minigame.MiniGameLifecycle
import gg.tropic.practice.minigame.MiniGameScoreboard
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.time.TimeUtil
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.material.Chest
import org.bukkit.util.Vector
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
class SkyWarsLifecycle(
    override val configuration: SkyWarsGameConfiguration,
    override val game: AbstractMiniGameGameImpl<SkyWarsGameConfiguration>
) : MiniGameLifecycle<SkyWarsGameConfiguration>, CompositeTerminable by CompositeTerminable.create()
{
    val events = LinkedList<GameEvent>().apply {
        this += FirstRefillGameEvent(this@SkyWarsLifecycle)
        this += SecondRefillGameEvent(this@SkyWarsLifecycle)
        this += DoomGameEvent(this@SkyWarsLifecycle)
        this += GameEndGameEvent(this@SkyWarsLifecycle)
    }

    var lastEventExecution: Long = 0L
    var previousTerminable: CompositeTerminable? = CompositeTerminable.create()

    val cages = mutableMapOf<UUID, LocalCage>()
    val chestController = ChestController(this)

    override val scoreboard: MiniGameScoreboard = SkyWarsScoreboard(game)
    override fun configure()
    {
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

        chestController.chests.putAll(
            game.map.metadata.metadata
                .filterIsInstance<MapChestMetadata>()
                .flatMap { it.locations.entries }
                .associate {
                    it.key to it.value
                }
        )

        println(chestController.chests)

        Events
            .subscribe(GameStartEvent::class.java)
            .filter { it.game.expectation == game.expectation }
            .handler {
                lastEventExecution = System.currentTimeMillis()

                cages.values.forEach { localCage ->
                    val arenaWorld = game.arenaWorld
                        ?: return@forEach

                    val baseBlock = localCage.location.toLocation(arenaWorld).block
                    localCage.cage.removeAll(baseBlock)
                }
                cages.clear()

                Schedulers
                    .sync()
                    .runRepeating({ task ->
                        if (!game.state(GameState.Playing))
                        {
                            task.closeAndReportException()
                            previousTerminable?.closeAndReportException()
                            return@runRepeating
                        }

                        val currentEvent = events.peekFirst()
                        if (currentEvent == null)
                        {
                            task.closeAndReportException()
                            previousTerminable?.closeAndReportException()
                            return@runRepeating
                        }

                        val estimatedExecution = currentEvent.durationPostGameStart
                            .plusMillis(game.startTimestamp)
                            .toMillis()

                        if (estimatedExecution <= System.currentTimeMillis())
                        {
                            previousTerminable?.closeAndReportException()
                            previousTerminable = null

                            currentEvent.run()
                            currentEvent.bindWith(this)

                            previousTerminable = currentEvent

                            events.pop()
                            lastEventExecution = System.currentTimeMillis()
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

    inner class SkyWarsScoreboard(override val game: AbstractMiniGameGameImpl<SkyWarsGameConfiguration>) :
        MiniGameScoreboard
    {
        override fun titleFor(player: Player) = "${CC.B_PRI}SKYWARS"

        fun Player.addSurrounding(board: MutableList<String>, surrounded: () -> List<String>)
        {
            board += surrounded()
            board += "      ${CC.GRAY}       ${CC.GRAY}"
            board += "${CC.GRAY}Players left: ${CC.GREEN}${
                game.allNonSpectators().size
            }"
            board += ""
            board += "${CC.GRAY}Kills: ${CC.PRI}0" // TODO: implement
            board += ""
            board += "${CC.GRAY}Map: ${CC.GREEN}${game.map.displayName}"
            board += "${CC.GRAY}Mode: ${CC.AQUA}Ranked"
        }

        override fun createWaitingScoreboardFor(player: Player, board: MutableList<String>)
        {
            board += "${CC.GRAY}Players: ${CC.PRI}${game.allNonSpectators().size}/4"
            board += ""
            board += "${CC.GRAY}Waiting for players..."
            board += ""
            board += "${CC.GRAY}Map: ${CC.GREEN}${game.map.displayName}"
            board += "${CC.GRAY}Mode: ${CC.AQUA}Ranked"
        }

        override fun createStartingScoreboardFor(player: Player, board: MutableList<String>)
        {
            player.addSurrounding(board) {
                listOf(
                    "${CC.GRAY}Next Event:",
                    "${CC.GREEN}Start ${TimeUtil.formatIntoMMSS(game.startCountDown)}"
                )
            }
        }

        override fun createInGameScoreboardFor(player: Player, board: MutableList<String>)
        {
            val nextEvent = events.peekFirst()
            player.addSurrounding(board) {
                listOf(
                    "${CC.GRAY}Next Event:",
                    "${CC.GREEN}${nextEvent?.describe() ?: "None"}"
                )
            }
        }

        override fun createEndingScoreboardFor(player: Player, report: GameReport?, board: MutableList<String>)
        {
            player.addSurrounding(board) {
                listOf(
                    "${CC.GRAY}Next Event:",
                    "${CC.GREEN}Game Ended!"
                )
            }
        }
    }
}
