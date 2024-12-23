package gg.scala.practice.games

import gg.scala.practice.expectation.GameExpectation
import gg.scala.practice.games.multiround.MultiRoundGame
import gg.scala.practice.games.team.GameTeam
import gg.scala.practice.kit.Kit
import java.util.*

/**
 * @author GrowlyX
 * @since 8/5/2022
 */
abstract class AbstractGame(
    val expectationModel: GameExpectation,
    val teams: Set<GameTeam>,
    val kit: Kit,
    val expectation: UUID = expectationModel.identifier
)
{
    val identifier: UUID
        get() = this.expectation

    var report: GameReport? = null
    var startTimestamp = -1L
    val multiRoundGame = MultiRoundGame()
}
