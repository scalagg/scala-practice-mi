package gg.solara.practice.minigame.hctranked.shared.pvpclass.classes

import gg.solara.practice.minigame.hctranked.shared.pvpclass.PvPClass
import net.evilblock.cubed.util.CC
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect

/**
 * @author Topu
 * @date 10/2/2024
 */
object DiamondClass : PvPClass() {

    override val name: String = "Diamond"
    override val color: String = CC.AQUA
    override val warmup: Long = 0L
    override val effects: Array<PotionEffect> = emptyArray()
    override val contents: Array<ItemStack?> = arrayOf(
        ItemStack(Material.DIAMOND_HELMET),
        ItemStack(Material.DIAMOND_CHESTPLATE),
        ItemStack(Material.DIAMOND_LEGGINGS),
        ItemStack(Material.DIAMOND_BOOTS),
    )
    override fun init() {}

    override fun onEnable(player: Player) {}
    override fun onDisable(player: Player) {}

}