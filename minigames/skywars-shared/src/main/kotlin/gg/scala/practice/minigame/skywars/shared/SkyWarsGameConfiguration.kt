package gg.scala.practice.minigame.skywars.shared

import gg.scala.practice.minigame.MiniGameConfiguration

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
