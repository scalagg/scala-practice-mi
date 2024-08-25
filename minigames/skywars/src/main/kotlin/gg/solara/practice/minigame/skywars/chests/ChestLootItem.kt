package gg.solara.practice.minigame.skywars.chests

import me.lucko.helper.random.Weighted
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
abstract class ChestLootItem : Weighted
{
    abstract val itemStack: ItemStack
}
