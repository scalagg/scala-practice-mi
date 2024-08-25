package gg.tropic.practice.minigame

import gg.tropic.practice.expectation.GameExpectation
import gg.tropic.practice.games.GameImpl
import gg.tropic.practice.kit.Kit

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
