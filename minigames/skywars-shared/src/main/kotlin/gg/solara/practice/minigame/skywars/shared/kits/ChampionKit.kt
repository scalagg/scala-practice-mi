package gg.solara.practice.minigame.skywars.shared.kits

import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

object ChampionKit : SkyWarsKit
{
    override val name = "Champion"
    override val icon = ItemBuilder.of(Material.DIAMOND_SWORD)

    override fun applyArmor(player: Player)
    {
    }

    override fun applyInventoryContents(player: Player)
    {
        player.inventory.addItem(
            ItemBuilder
                .of(Material.DIAMOND_SWORD)
                .name("${CC.AQUA}Champion's Diamond Sword")
                .enchant(Enchantment.DAMAGE_ALL, 2)
                .build()
        )

        player.inventory.addItem(
            ItemBuilder
                .of(Material.ENCHANTED_BOOK)
                .enchant(Enchantment.DAMAGE_ALL, 1)
                .amount(3)
                .build()
        )

        player.inventory.addItem(
            ItemBuilder
                .of(Material.ANVIL)
                .build()
        )
    }
}
