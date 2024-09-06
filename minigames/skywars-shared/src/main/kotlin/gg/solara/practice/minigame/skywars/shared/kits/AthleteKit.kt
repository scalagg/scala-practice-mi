package gg.solara.practice.minigame.skywars.shared.kits

import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.potion.Potion
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
object AthleteKit : SkyWarsKit
{
    override val name = "Athlete"
    override val icon = ItemBuilder.of(Material.DIAMOND_SWORD)

    override fun applyArmor(player: Player)
    {
    }

    override fun applyInventoryContents(player: Player)
    {
        player.inventory.addItem(
            ItemBuilder
                .of(Material.DIAMOND_SWORD)
                .build()
        )

        player.inventory.addItem(
            ItemBuilder
                .of(Material.FISHING_ROD)
                .build()
        )

        player.inventory.addItem(
            ItemBuilder
                .of(Material.WATER_BUCKET)
                .build()
        )

        val strengthPotion = Potion(PotionType.STRENGTH)
        strengthPotion.effects += PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 0)

        player.inventory.addItem(
            ItemBuilder
                .copyOf(strengthPotion.toItemStack(1))
                .name("${CC.WHITE}Potion of Strength (+20%) (0:40)")
                .build()
        )

        val speedPotion = Potion(PotionType.SPEED).splash()
        speedPotion.effects += PotionEffect(PotionEffectType.SPEED, 4, 4)

        player.inventory.addItem(
            ItemBuilder
                .copyOf(speedPotion.toItemStack(1))
                .name("${CC.WHITE}Splash Potion of Speed III (0:04)")
                .amount(2)
                .build()
        )

        player.inventory.addItem(
            ItemBuilder
                .of(Material.GOLDEN_APPLE)
                .amount(2)
                .build()
        )
    }
}
