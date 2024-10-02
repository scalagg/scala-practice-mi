package gg.solara.practice.minigame.hctranked.shared.pvpclass.classes

import gg.solara.practice.minigame.hctranked.shared.pvpclass.PvPClass
import net.evilblock.cubed.util.CC
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * @author Topu
 * @date 10/2/2024
 */
object BardClass : PvPClass() {

    override val name: String = "Bard"
    override val color: String = CC.YELLOW
    override val warmup: Long = 3000L
    override val effects: Array<PotionEffect> = arrayOf(
        PotionEffect(PotionEffectType.SPEED,Integer.MAX_VALUE,1),
        PotionEffect(PotionEffectType.REGENERATION,Integer.MAX_VALUE,0),
        PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,Integer.MAX_VALUE,1)
    )
    override val contents: Array<ItemStack?> = arrayOf(
        ItemStack(Material.GOLD_HELMET),
        ItemStack(Material.GOLD_CHESTPLATE),
        ItemStack(Material.GOLD_LEGGINGS),
        ItemStack(Material.GOLD_BOOTS),
    )
    override fun init() {}

    override fun onEnable(player: Player) {}
    override fun onDisable(player: Player) {}

}