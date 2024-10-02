package gg.solara.practice.minigame.hctranked.shared.pvpclass

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

/**
 * @author Topu
 * @date 10/2/2024
 */
abstract class PvPClass {

    abstract val name: String
    abstract val warmup: Long
    abstract val color: String

    abstract val effects: Array<PotionEffect>
    abstract val contents: Array<ItemStack?>

    val players = hashSetOf<Player>()

    private val effectsByType = hashMapOf<PotionEffectType,PotionEffect>()

    open fun load() {
        this.effects.forEach{this.effectsByType[it.type] = it}
    }

    abstract fun init()

    abstract fun onEnable(player: Player)
    abstract fun onDisable(player: Player)

    operator fun get(type: PotionEffectType):PotionEffect? {
        return this.effectsByType[type]
    }

    fun isEquipped(player: Player):Boolean {
        return player.inventory.helmet?.type == this.contents[0]?.type
            && player.inventory.chestplate?.type  == this.contents[1]?.type
            && player.inventory.leggings?.type  == this.contents[2]?.type
            && player.inventory.boots?.type  == this.contents[3]?.type
    }

    override fun equals(other: Any?): Boolean {
        return other is PvPClass && this.name == other.name
    }

    override fun hashCode(): Int {
        return Objects.hash(this.name)
    }

}