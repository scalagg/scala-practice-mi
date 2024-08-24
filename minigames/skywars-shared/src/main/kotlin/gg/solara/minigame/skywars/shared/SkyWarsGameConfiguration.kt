package gg.solara.minigame.skywars.shared

import gg.tropic.practice.minigame.MiniGameConfiguration

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
class SkyWarsGameConfiguration(
    val mode: SkyWarsMode
) : MiniGameConfiguration
{
    override fun getAbstractType() = SkyWarsGameConfiguration::class.java
}
