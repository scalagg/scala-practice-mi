package gg.solara.practice.minigame.hctranked.shared

import gg.tropic.practice.minigame.MiniGameConfiguration

/**
 * @author Topu
 * @date 10/2/2024
 */
class HCTRankedGameConfiguration(
    val mode: HCTRankedMode
) : MiniGameConfiguration
{
    override fun getAbstractType() = HCTRankedGameConfiguration::class.java
}