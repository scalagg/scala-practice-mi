package gg.scala.practice.minigame.skywars.shared.kits

import me.lucko.helper.utils.entityspawner.EntitySpawner
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.entity.Wolf
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object HoundKit : SkyWarsKit
{
    override val name = "Hound"
    override val icon = ItemBuilder.of(Material.BONE)

    override fun applyArmor(player: Player) {
        player.inventory.boots = ItemBuilder
            .of(Material.DIAMOND_BOOTS)
            .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2) // Protection II
            .build()
    }

    override fun applyInventoryContents(player: Player) {
        player.inventory.addItem(
            ItemBuilder
                .of(Material.DIAMOND_SWORD)
                .build()
        )

        player.inventory.addItem(
            ItemBuilder
                .of(Material.COOKED_BEEF)
                .amount(16)
                .build()
        )

        EntitySpawner.INSTANCE.spawn(player.location, Wolf::class.java) { wolf ->
            wolf.isTamed = true
            wolf.owner = player
            wolf.health = 20.0
            wolf.customName = "${CC.GREEN}${player.name}'s Wolf"
            wolf.isCustomNameVisible = true
            wolf.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Int.MAX_VALUE, 0))
        }
    }
}
