package gg.solara.practice.minigame.hctranked

import gg.scala.flavor.service.Service
import gg.solara.practice.minigame.hctranked.shared.HCTRankedGameConfiguration
import gg.tropic.practice.minigame.AbstractMiniGameGameImpl
import gg.tropic.practice.minigame.BasicMiniGameOrchestrator
import gg.tropic.practice.minigame.MiniGameLifecycle

/**
 * @author Topu
 * @date 10/2/2024
 */
@Service
object HCTRankedGameOrchestrator : BasicMiniGameOrchestrator<HCTRankedGameConfiguration>()
{
    override fun prepare(
        miniGame: AbstractMiniGameGameImpl<HCTRankedGameConfiguration>,
        configuration: HCTRankedGameConfiguration
    ): MiniGameLifecycle<HCTRankedGameConfiguration>
    {
        TODO("Not yet implemented")
    }

    override val id: String
        get() = TODO("Not yet implemented")
    override val minimumPlayersRequiredToEnterStarting: Int
        get() = TODO("Not yet implemented")
    override val startGameCountDown: Int
        get() = TODO("Not yet implemented")
}