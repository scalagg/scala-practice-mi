package gg.solara.practice.minigame.hctranked.shared

import org.bukkit.Material

/**
 * @author Topu
 * @date 10/2/2024
 */
enum class HCTRankedMode(
    val displayName: String,
    val material: Material
)
{
    ORIGINAL("Original", Material.GOLD_CHESTPLATE),
    ALL_DIAMOND("All Diamond", Material.DIAMOND_CHESTPLATE)
}