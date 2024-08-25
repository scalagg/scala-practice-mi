package gg.tropic.practice.minigame

import gg.tropic.practice.games.GameImpl
import gg.tropic.practice.games.GameReport
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/19/2024
 */
interface MiniGameScoreboard
{
    val game: GameImpl

    fun createWaitingScoreboardFor(player: Player, board: List<String>)
    fun createInGameScoreboardFor(player: Player, board: List<String>)
    fun createEndingScoreboardFor(player: Player, report: GameReport?, board: List<String>)
}
