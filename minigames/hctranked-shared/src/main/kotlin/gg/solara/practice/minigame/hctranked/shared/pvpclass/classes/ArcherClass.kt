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
object ArcherClass : PvPClass() {

    override val name: String = "Archer"
    override val warmup: Long = 3000L
    override val color: String = CC.DARK_PURPLE
    override val effects: Array<PotionEffect> = arrayOf(
        PotionEffect(PotionEffectType.SPEED,Integer.MAX_VALUE,2),
        PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,Integer.MAX_VALUE,1)
    )
    override val contents: Array<ItemStack?> = arrayOf(
        ItemStack(Material.LEATHER_HELMET),
        ItemStack(Material.LEATHER_CHESTPLATE),
        ItemStack(Material.LEATHER_LEGGINGS),
        ItemStack(Material.LEATHER_BOOTS),
    )
    override fun init() {}

    override fun onEnable(player: Player) {}
    override fun onDisable(player: Player) {}

}