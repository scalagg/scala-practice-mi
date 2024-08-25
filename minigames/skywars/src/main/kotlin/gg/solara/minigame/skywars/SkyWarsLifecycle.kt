package gg.solara.minigame.skywars

import gg.solara.minigame.skywars.events.FirstRefillGameEvent
import gg.solara.minigame.skywars.events.GameEvent
import gg.solara.minigame.skywars.shared.SkyWarsGameConfiguration
import gg.tropic.practice.games.GameReport
import gg.tropic.practice.games.GameState
import gg.tropic.practice.map.metadata.impl.MapChestMetadata
import gg.tropic.practice.minigame.AbstractMiniGameGameImpl
import gg.tropic.practice.minigame.MiniGameLifecycle
import gg.tropic.practice.minigame.MiniGameScoreboard
import me.lucko.helper.Schedulers
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.time.TimeUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.LinkedList

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
    }

    override val scoreboard: MiniGameScoreboard = SkyWarsScoreboard(game)
    override fun configure()
    {
        Schedulers
            .sync()
            .runRepeating({ task ->
                if (!game.state(GameState.Playing))
                {
                    return@runRepeating
                }

                val currentEvent = events.peekFirst()
                val estimatedExecution = currentEvent.durationPostGameStart
                    .plusMillis(game.startTimestamp)
                    .toMillis()

                if (estimatedExecution <= System.currentTimeMillis())
                {
                    currentEvent.run()
                    events.pop()
                }
            }, 0L, 20L)
            .bindWith(this)

        val world = game.arenaWorld
            ?: return
        game.map.metadata.metadata
            .filterIsInstance<MapChestMetadata>()
            .forEach {
                it.locations.values.flatten().forEach { position ->
                    val location = position.toLocation(world)

                    world.getBlockAt(location)
                        .setType(Material.CHEST, true)
                }
            }
    }

    inner class SkyWarsScoreboard(override val game: AbstractMiniGameGameImpl<SkyWarsGameConfiguration>) : MiniGameScoreboard
    {
        override fun titleFor(player: Player) = "${CC.B_PRI}SkyWars"

        fun Player.addSurrounding(board: MutableList<String>, surrounded: () -> List<String>)
        {
            board += surrounded()
            board += ""
            board += "${CC.GRAY}Players left: ${CC.PRI}${
                game.allNonSpectators().size
            }"
            board += ""
            board += "${CC.GRAY}Kills: ${CC.PRI}0" // TODO: implement
            board += ""
            board += "${CC.GRAY}Map: ${CC.PRI}${game.map.displayName}"
            board += "${CC.GRAY}Mode: ${CC.AQUA}Ranked"
        }

        override fun createWaitingScoreboardFor(player: Player, board: MutableList<String>)
        {
            board += "${CC.GRAY}Players: ${CC.GREEN}${
                game.allNonSpectators().size
            }/4"
            board += ""
            board += "${CC.GRAY}Waiting..."
            board += ""
            board += "${CC.GRAY}Map: ${CC.PRI}${game.map.displayName}"
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
