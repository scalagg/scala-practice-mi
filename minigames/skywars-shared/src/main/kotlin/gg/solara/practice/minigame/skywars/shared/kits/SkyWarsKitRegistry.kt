package gg.solara.practice.minigame.skywars.shared.kits

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
object SkyWarsKitRegistry
{
    val kits = setOf(
        ArmorerKit,
        AthleteKit,
        BlacksmithKit,
        BowmanKit,
        ChampionKit,
        HealerKit,
        HoundKit,
        MagicianKit,
        PaladinKit,
        PyromancerKit,
        ScoutKit
    ).associateBy { it.name }
}
