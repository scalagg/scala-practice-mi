package gg.solara.practice.minigame.skywars.shared.kits

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
object SkyWarsKitRegistry
{
    val kits = setOf(
        ArmorerKit,
        ScoutKit,
        BowmanKit
    ).associateBy { it.name }
}
