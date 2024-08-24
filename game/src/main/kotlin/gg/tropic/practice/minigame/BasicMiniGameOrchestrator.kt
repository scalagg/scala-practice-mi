package gg.tropic.practice.minigame

import gg.tropic.practice.expectation.GameExpectation
import gg.tropic.practice.kit.Kit

/**
 * @author GrowlyX
 * @since 8/23/2024
 */
abstract class BasicMiniGameOrchestrator<T : MiniGameConfiguration> : MiniGameOrchestrator<T>
{
    abstract fun prepare(miniGame: AbstractMiniGameGameImpl<T>, configuration: T): MiniGameLifecycle<T>
    override fun construct(kit: Kit, expectation: GameExpectation): AbstractMiniGameGameImpl<T>
    {
        return object : AbstractMiniGameGameImpl<T>(expectation, kit)
        {
            override fun prepare(configuration: T) = this@BasicMiniGameOrchestrator.prepare(this, configuration)
        }
    }
}
