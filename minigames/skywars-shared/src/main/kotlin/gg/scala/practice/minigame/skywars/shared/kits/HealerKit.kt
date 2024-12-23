package gg.scala.practice.minigame.skywars.shared.kits

import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.potion.Potion
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType

object HealerKit : SkyWarsKit
{
    override val name = "Healer"
    override val icon = ItemBuilder.of(Material.GOLDEN_APPLE)

    override fun applyArmor(player: Player)
    {
        // No specific armor for HealerKit; you can add if needed
    }

    override fun applyInventoryContents(player: Player)
    {
        val regenPotion = Potion(PotionType.REGEN).splash()
        regenPotion.effects += PotionEffect(PotionEffectType.REGENERATION, 12, 1) // Regeneration II (0:12)

        player.inventory.addItem(
            ItemBuilder
                .copyOf(regenPotion.toItemStack(1))
                .name("${CC.WHITE}Splash Potion of Regeneration II (0:12)")
                .amount(2)
                .build()
        )

        val healPotion = Potion(PotionType.INSTANT_HEAL).splash()
        healPotion.effects += PotionEffect(PotionEffectType.HEAL, 1, 1) // Heal (2 Hearts)

        player.inventory.addItem(
            ItemBuilder
                .copyOf(healPotion.toItemStack(1))
                .name("${CC.WHITE}Splash Potion of Heal (2 Hearts)")
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
