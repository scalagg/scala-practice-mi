package gg.scala.practice.minigame.skywars.shared.kits

import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.potion.Potion
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType

object PyromancerKit : SkyWarsKit
{
    override val name = "Pyromancer"
    override val icon = ItemBuilder.of(Material.FLINT_AND_STEEL)

    override fun applyArmor(player: Player)
    {
        player.inventory.boots = ItemBuilder
            .of(Material.DIAMOND_BOOTS)
            .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
            .enchant(Enchantment.PROTECTION_FIRE, 4)
            .build()
    }

    override fun applyInventoryContents(player: Player)
    {
        player.inventory.addItem(
            ItemBuilder
                .of(Material.DIAMOND_SWORD)
                .enchant(Enchantment.FIRE_ASPECT, 2)
                .build()
        )

        val fireResistancePotion = Potion(PotionType.FIRE_RESISTANCE).splash()
        fireResistancePotion.effects += PotionEffect(PotionEffectType.FIRE_RESISTANCE, 90, 0)

        player.inventory.addItem(
            ItemBuilder
                .copyOf(fireResistancePotion.toItemStack(1))
                .name("${CC.WHITE}Splash Potion of Fire Resistance (1:30)")
                .build()
        )

        player.inventory.addItem(
            ItemBuilder
                .of(Material.LAVA_BUCKET)
                .amount(2)
                .build()
        )
    }
}
