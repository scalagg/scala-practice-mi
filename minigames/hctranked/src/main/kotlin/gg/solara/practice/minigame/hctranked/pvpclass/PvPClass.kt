package gg.solara.practice.minigame.hctranked.shared.pvpclass

import com.google.common.collect.HashBasedTable
import gg.scala.commons.annotations.Listeners
import me.lucko.helper.Schedulers
import net.evilblock.cubed.entity.living.effect.event.EntityEffectExpiredEvent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.inventory.PlayerInventory
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

/**
 * @author Topu
 * @date 10/2/2024
 */
@Listeners
abstract class PvPClass(
    val name: String,
    var warmup: Int,
    val armorContains: String,
    val consumables: List<Material>?
)
{
    companion object {
        private val restores = HashBasedTable.create<UUID, PotionEffectType, PotionEffect>()

        @JvmStatic
        fun removeInfiniteEffects(player: Player) {
            for (potionEffect in player.activePotionEffects) {
                if (potionEffect.duration > 1_000_000) {
                    player.removePotionEffect(potionEffect.type)
                }
            }
        }

        @JvmStatic
        fun smartAddPotion(player: Player, potionEffect: PotionEffect, persistOldValues: Boolean, pvpClass: PvPClass) {
            setRestoreEffect(player, potionEffect)
        }

        @JvmStatic
        fun setRestoreEffect(player: Player, effect: PotionEffect) {
            var shouldCancel = true
            val activeList = player.activePotionEffects
            for (active in activeList) {
                if (active.type != effect.type) continue

                if (effect.amplifier < active.amplifier) {
                    return
                } else if (effect.amplifier == active.amplifier) {
                    if (0 < active.duration && (effect.duration <= active.duration || effect.duration - active.duration < 10)) {
                        return
                    }
                }

                restores.put(player.uniqueId, active.type, active)
                shouldCancel = false
                break
            }

            player.addPotionEffect(effect, true)
            if (shouldCancel && effect.duration > 120 && effect.duration < 9600) {
                restores.remove(player.uniqueId, effect.type)
            }
        }
    }

    init {
        this.warmup = 5
    }

    open fun apply(player: Player) {
    }

    open fun tick(player: Player) {
    }

    open fun remove(player: Player) {
    }

    open fun canApply(player: Player): Boolean {
        return true
    }

    open fun itemConsumed(player: Player, type: Material): Boolean {
        return true
    }

    fun qualifies(armor: PlayerInventory): Boolean {
        return armor.helmet != null && armor.chestplate != null && armor.leggings != null && armor.boots != null &&
            armor.helmet!!.type.name.startsWith(armorContains) &&
            armor.chestplate!!.type.name.startsWith(armorContains) &&
            armor.leggings!!.type.name.startsWith(armorContains) &&
            armor.boots!!.type.name.startsWith(armorContains)
    }

    data class SavedPotion(
        val potionEffect: PotionEffect,
        val time: Long,
        val permanent: Boolean
    )

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPotionEffectExpire(event: EntityEffectExpiredEvent) {
        val livingEntity = event.entity
        if (livingEntity is Player) {
            val player = livingEntity
            val previous = restores.remove(player.uniqueId, event.effect.effectType)
            if (previous != null && previous.duration < 1_000_000) {
                Schedulers.sync().runLater({
                    player.addPotionEffect(previous, true)
                }, 1)
            }
        }
    }
}