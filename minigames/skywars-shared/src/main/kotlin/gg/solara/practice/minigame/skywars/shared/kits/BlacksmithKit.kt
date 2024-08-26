package gg.solara.practice.minigame.skywars.shared.kits

import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

object BlacksmithKit : SkyWarsKit
{
    override val name = "Blacksmith"
    override val icon = ItemBuilder.of(Material.ANVIL)

    override fun applyArmor(player: Player)
    {
        val randomArmorPiece = listOf(
            player.inventory::setHelmet to Material.DIAMOND_HELMET,
            player.inventory::setChestplate to Material.DIAMOND_CHESTPLATE,
            player.inventory::setLeggings to Material.DIAMOND_LEGGINGS,
            player.inventory::setBoots to Material.DIAMOND_BOOTS
        ).random()

        randomArmorPiece.first(
            ItemBuilder
                .of(randomArmorPiece.second)
                .build()
        )
    }

    override fun applyInventoryContents(player: Player)
    {
        val randomEnchantment = when ((1..4).random())
        {
            1 -> Enchantment.DAMAGE_ALL to 1 // Sharpness I
            2 -> Enchantment.KNOCKBACK to 1  // Knockback I
            3 -> Enchantment.PROTECTION_ENVIRONMENTAL to 1 // Protection I
            else -> Enchantment.PROTECTION_PROJECTILE to 1 // Projectile Protection I
        }

        val randomEnchantmentBook = when ((1..3).random())
        {
            1 -> Enchantment.DAMAGE_ALL to 1 // Sharpness I
            2 -> Enchantment.ARROW_DAMAGE to 1 // Power I
            else -> Enchantment.PROTECTION_ENVIRONMENTAL to 1 // Protection I
        }

        player.inventory.addItem(
            ItemBuilder
                .of(Material.ANVIL)
                .build()
        )

        player.level = 15

        val firstBook = ItemBuilder
            .of(Material.ENCHANTED_BOOK)
            .enchant(randomEnchantment.first, randomEnchantment.second)
            .build()

        val secondBook = ItemBuilder
            .of(Material.ENCHANTED_BOOK)
            .enchant(randomEnchantmentBook.first, randomEnchantmentBook.second)
            .build()

        player.inventory.addItem(firstBook)
        player.inventory.addItem(secondBook)
    }
}
