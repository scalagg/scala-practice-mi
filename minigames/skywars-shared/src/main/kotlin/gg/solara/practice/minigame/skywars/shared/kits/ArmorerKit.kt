package gg.solara.practice.minigame.skywars.shared.kits

import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
object ArmorerKit : SkyWarsKit
{
    override val name = "Armorer"
    override val icon = ItemBuilder.of(Material.IRON_CHESTPLATE)

    override fun applyArmor(player: Player)
    {
        player.inventory.chestplate = ItemBuilder
            .of(Material.DIAMOND_CHESTPLATE)
            .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            .build()

        player.inventory.boots = ItemBuilder
            .of(Material.DIAMOND_BOOTS)
            .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            .build()
    }

    override fun applyInventoryContents(player: Player)
    {

    }
}
