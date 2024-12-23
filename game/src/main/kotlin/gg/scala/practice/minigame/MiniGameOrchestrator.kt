package gg.scala.practice.minigame

import gg.scala.practice.expectation.GameExpectation
import gg.scala.practice.kit.Kit

/**
 * @author GrowlyX
 * @since 8/19/2024
 */
interface MiniGameOrchestrator<T : MiniGameConfiguration>
{
    val id: String
    val minimumPlayersRequiredToEnterStarting: Int
    val startGameCountDown: Int

    fun construct(kit: Kit, expectation: GameExpectation): AbstractMiniGameGameImpl<T>
}
