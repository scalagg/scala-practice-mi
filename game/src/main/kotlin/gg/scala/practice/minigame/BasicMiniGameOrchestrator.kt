package gg.scala.practice.minigame

import gg.scala.practice.expectation.GameExpectation
import gg.scala.practice.kit.Kit

/**
 * @author GrowlyX
 * @since 8/23/2024
 */
abstract class BasicMiniGameOrchestrator<T : MiniGameConfiguration> : MiniGameOrchestrator<T>
{
    abstract fun prepare(miniGame: AbstractMiniGameGameImpl<T>, configuration: T): MiniGameLifecycle<T>
    override fun construct(kit: Kit, expectation: GameExpectation): AbstractMiniGameGameImpl<T>
    {
        return object : AbstractMiniGameGameImpl<T>(this, expectation, kit)
        {
            override fun prepare(configuration: T) = this@BasicMiniGameOrchestrator.prepare(this, configuration)
                .apply {
                    bindWith(game)
                }
        }
    }
}
