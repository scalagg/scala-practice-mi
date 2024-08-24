package gg.tropic.practice.minigame

import gg.tropic.practice.expectation.GameExpectation
import gg.tropic.practice.kit.Kit

/**
 * @author GrowlyX
 * @since 8/19/2024
 */
interface MiniGameOrchestrator<T : MiniGameConfiguration>
{
    fun id(): String
    fun construct(kit: Kit, expectation: GameExpectation): AbstractMiniGameGameImpl<T>
}
