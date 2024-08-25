package gg.solara.minigame.skywars

import gg.scala.lemon.util.SplitUtil
import gg.solara.minigame.skywars.events.GameEvent
import gg.solara.minigame.skywars.shared.SkyWarsGameConfiguration
import gg.tropic.practice.games.GameImpl
import gg.tropic.practice.games.GameReport
import gg.tropic.practice.minigame.AbstractMiniGameGameImpl
import gg.tropic.practice.minigame.MiniGameLifecycle
import gg.tropic.practice.minigame.MiniGameScoreboard
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.time.TimeUtil
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
class SkyWarsLifecycle(
    override val configuration: SkyWarsGameConfiguration,
    override val game: AbstractMiniGameGameImpl<SkyWarsGameConfiguration>
) : MiniGameLifecycle<SkyWarsGameConfiguration>, CompositeTerminable by CompositeTerminable.create()
{
    val events = linkedSetOf<GameEvent>()

    override val scoreboard: MiniGameScoreboard = SkyWarsScoreboard(game)
    override fun configure()
    {

    }

    inner class SkyWarsScoreboard(override val game: AbstractMiniGameGameImpl<SkyWarsGameConfiguration>) : MiniGameScoreboard
    {
        override fun titleFor(player: Player) = "${CC.B_PRI}SkyWars"

        fun Player.addSurrounding(board: MutableList<String>, surrounded: () -> List<String>)
        {
            board += "${CC.D_GRAY}${SplitUtil.splitUuid(game.expectation)}"
            board += ""
            board += surrounded()
            board += ""
            board += "${CC.GRAY}Players left: ${CC.SEC}${
                game.allNonSpectators().size
            }"
            board += ""
            board += "${CC.GRAY}Kills: ${CC.SEC}0" // TODO: implement
            board += ""
            board += "${CC.GRAY}Map: ${CC.SEC}${game.map.displayName}"
            board += "${CC.GRAY}Mode: ${CC.AQUA}Ranked"
        }

        override fun createWaitingScoreboardFor(player: Player, board: MutableList<String>)
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
            player.addSurrounding(board) {
                listOf(
                    "${CC.GRAY}Next Event:",
                    "${CC.GREEN}Refill ${TimeUtil.formatIntoMMSS(game.startCountDown)}"
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
