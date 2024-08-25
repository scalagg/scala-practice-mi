package gg.solara.minigame.skywars.shared.cages

import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
data class Cage(
    val id: String,
    val displayName: String,
    val item: ItemStack,
    var description: List<String>,
    val linkedSchema: String,
    var price: Float,
    var rarity: CageRarity,
    var purchasable: Boolean = true
)
