package gg.solara.practice.minigame.skywars.shared.kits

import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.potion.Potion
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
object ScoutKit : SkyWarsKit
{
    override val name = "Scout"
    override val icon = ItemBuilder.of(Material.DIAMOND_AXE)

    override fun applyArmor(player: Player)
    {
        player.inventory.boots = ItemBuilder
            .of(Material.DIAMOND_BOOTS)
            .enchant(Enchantment.PROTECTION_FALL, 1)
            .build()
    }

    override fun applyInventoryContents(player: Player)
    {
        player.inventory.addItem(
            ItemBuilder
                .of(Material.DIAMOND_AXE)
                .enchant(Enchantment.DIG_SPEED, 2)
                .enchant(Enchantment.DAMAGE_ALL, 1)
                .build()
        )

        player.inventory.addItem(
            ItemBuilder
                .of(Material.DIAMOND_PICKAXE)
                .enchant(Enchantment.DIG_SPEED, 2)
                .build()
        )

        val potion = Potion(PotionType.SPEED).splash()
        potion.effects += PotionEffect(PotionEffectType.SPEED, 30, 3)

        val itemStack = potion.toItemStack(3)
        val meta = itemStack.itemMeta
        meta.displayName = "${CC.WHITE}Splash Potion of Speed II (0:30)"
        itemStack.itemMeta = meta

        player.inventory.addItem(itemStack)
    }
}
