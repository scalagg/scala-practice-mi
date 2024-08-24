package gg.solara.minigame.skywars

import gg.scala.flavor.service.Service
import gg.solara.minigame.skywars.shared.SkyWarsGameConfiguration
import gg.tropic.practice.minigame.AbstractMiniGameGameImpl
import gg.tropic.practice.minigame.BasicMiniGameOrchestrator

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
@Service
object SkyWarsGameOrchestrator : BasicMiniGameOrchestrator<SkyWarsGameConfiguration>()
{
    override fun prepare(
        miniGame: AbstractMiniGameGameImpl<SkyWarsGameConfiguration>,
        configuration: SkyWarsGameConfiguration
    ) = SkyWarsLifecycle(configuration, miniGame)

    override fun id() = "skywars"
}
