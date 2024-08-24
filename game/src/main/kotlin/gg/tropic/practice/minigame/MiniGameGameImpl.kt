package gg.tropic.practice.minigame

import gg.tropic.practice.expectation.GameExpectation
import gg.tropic.practice.games.GameImpl
import gg.tropic.practice.kit.Kit
import gg.tropic.practice.kit.feature.FeatureFlag

/**
 * @author GrowlyX
 * @since 8/23/2024
 */
class MiniGameGameImpl<T : MiniGameConfiguration>(expectation: GameExpectation, kit: Kit) : GameImpl(expectation, kit)
{
    override fun buildResources(): Boolean
    {
        val orch = (MiniGameRegistry
            .miniGameOrchestrators[flagMetaData(FeatureFlag.MiniGameType, "id")] as MiniGameOrchestrator<T>?)
            ?: return false
        val miniGameConfig = expectationModel.miniGameConfiguration
            ?: return false

        miniGameLifecycle = orch.prepare(this, miniGameConfig as T)
        return super.buildResources()
    }
}
