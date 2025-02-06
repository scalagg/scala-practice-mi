package gg.scala.practice.games.features

import gg.scala.practice.games.GameService
import gg.scala.practice.games.GameState
import net.evilblock.cubed.util.CC
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * @author Topu
 * @date 2/6/2025
 */
class TotemFeature : Listener
{
    @EventHandler
    fun damage(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return
        if (player.health - event.finalDamage > 0) return

        val game = GameService.byPlayer(player) ?: return

        if (!game.state(GameState.Playing)) {
            player.sendMessage("${CC.RED}You cannot use totems right now!")
            return
        }

        for (i in 0..8)
        {
            val item = player.inventory.getItem(i)
            if (item?.type == Material.GOLD_INGOT && item.itemMeta?.displayName == "${CC.GOLD}Totem of Undying") {
                event.isCancelled = true
                item.amount -= 1
                player.health = player.maxHealth
                player.sendMessage("${CC.GOLD}Your Totem saved you!")
                player.world.playSound(player.location, Sound.LEVEL_UP, 1f, 1f)
                player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 200, 1))
                player.addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, 2400, 1))
                player.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 600, 0))
                return
            }
        }
    }
}

