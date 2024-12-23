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

object PaladinKit : SkyWarsKit
{
    override val name = "Paladin"
    override val icon = ItemBuilder.of(Material.GOLD_INGOT)

    override fun applyArmor(player: Player) {
        player.inventory.leggings = ItemBuilder
            .of(Material.DIAMOND_LEGGINGS)
            .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2) // Protection II
            .build()
    }

    override fun applyInventoryContents(player: Player) {
        val resistanceRegenPotion = Potion(PotionType.REGEN).splash()
        resistanceRegenPotion.effects += PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 15, 0) // Resistance I (0:15)
        resistanceRegenPotion.effects += PotionEffect(PotionEffectType.REGENERATION, 8, 0) // Regeneration I (0:08)

        player.inventory.addItem(
            ItemBuilder
                .copyOf(resistanceRegenPotion.toItemStack(1))
                .name("${CC.WHITE}Paladin Potion")
                .addToLore(
                    "${CC.GRAY}Applies Resistance I (0:15)",
                    "${CC.GRAY}Applies Regeneration I (0:08)"
                )
                .amount(3)
                .build()
        )
    }
}
