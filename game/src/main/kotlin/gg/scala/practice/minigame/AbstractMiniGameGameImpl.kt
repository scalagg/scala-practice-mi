package gg.scala.practice.minigame

import gg.scala.practice.expectation.GameExpectation
import gg.scala.practice.games.GameImpl
import gg.scala.practice.kit.Kit

/**
 * @author GrowlyX
 * @since 8/23/2024
 */
abstract class AbstractMiniGameGameImpl<T : MiniGameConfiguration>(
    val orchestrator: BasicMiniGameOrchestrator<T>,
    expectation: GameExpectation, kit: Kit
) : GameImpl(expectation, kit)
{
    var startCountDown = orchestrator.startGameCountDown
    abstract fun prepare(configuration: T): MiniGameLifecycle<T>

    override fun buildResources(): Boolean
    {
        val miniGameConfig = expectationModel.miniGameConfiguration
            ?: return false

        miniGameLifecycle = prepare(miniGameConfig as T)
        miniGameLifecycle?.configure()

        return super.buildResources()
    }
}
