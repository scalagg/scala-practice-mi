package gg.scala.practice.minigame

import gg.scala.practice.games.GameReport
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/19/2024
 */
interface MiniGameScoreboard
{
    val game: AbstractMiniGameGameImpl<*>

    fun titleFor(player: Player): String

    fun createWaitingScoreboardFor(player: Player, board: MutableList<String>)
    fun createStartingScoreboardFor(player: Player, board: MutableList<String>)
    fun createInGameScoreboardFor(player: Player, board: MutableList<String>)
    fun createEndingScoreboardFor(player: Player, report: GameReport?, board: MutableList<String>)
}
