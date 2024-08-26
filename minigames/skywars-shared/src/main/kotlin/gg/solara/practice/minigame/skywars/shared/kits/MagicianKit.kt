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

object MagicianKit : SkyWarsKit
{
    override val name = "Magician"
    override val icon = ItemBuilder.of(Material.BLAZE_POWDER)

    override fun applyArmor(player: Player)
    {
        player.inventory.chestplate = ItemBuilder
            .of(Material.DIAMOND_CHESTPLATE)
            .enchant(Enchantment.PROTECTION_FIRE, 2)
            .build()
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
                .of(Material.MILK_BUCKET)
                .amount(2)
                .build()
        )

        val weaknessPoisonPotion = Potion(PotionType.WEAKNESS).splash()
        weaknessPoisonPotion.effects += PotionEffect(PotionEffectType.WEAKNESS, 8, 0)
        weaknessPoisonPotion.effects += PotionEffect(PotionEffectType.POISON, 8, 0)

        player.inventory.addItem(
            ItemBuilder
                .copyOf(weaknessPoisonPotion.toItemStack(1))
                .name("${CC.WHITE}Splash Potion of Weakness I, Poison I (0:08)")
                .amount(2)
                .build()
        )

        val instantDamagePotion = Potion(PotionType.INSTANT_DAMAGE).splash()
        instantDamagePotion.effects += PotionEffect(PotionEffectType.HARM, 1, 0)

        player.inventory.addItem(
            ItemBuilder
                .copyOf(instantDamagePotion.toItemStack(1))
                .name("${CC.WHITE}Splash Potion of Instant Damage I (2 Hearts)")
                .amount(2)
                .build()
        )
    }
}
