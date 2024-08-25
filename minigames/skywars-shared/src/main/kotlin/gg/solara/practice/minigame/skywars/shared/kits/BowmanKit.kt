package gg.solara.practice.minigame.skywars.shared.kits

import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
object BowmanKit : SkyWarsKit
{
    override val name = "Bowman"
    override val icon = ItemBuilder.of(Material.BOW)

    override fun applyArmor(player: Player)
    {
        player.inventory.helmet = ItemBuilder
            .of(Material.DIAMOND_HELMET)
            .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
            .build()
    }

    override fun applyInventoryContents(player: Player)
    {
        player.inventory.addItem(
            ItemBuilder
                .of(Material.BOW)
                .enchant(Enchantment.ARROW_DAMAGE, 3)
                .build()
        )

        player.inventory.addItem(
            ItemBuilder
                .of(Material.ARROW)
                .amount(32)
                .build()
        )
    }
}
