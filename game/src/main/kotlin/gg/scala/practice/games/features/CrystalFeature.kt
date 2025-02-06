package gg.scala.practice.games.features

import gg.scala.practice.games.GameService
import gg.scala.practice.games.GameState
import net.evilblock.cubed.util.CC
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*


/**
 * @author Topu
 * @date 2/6/2025
 */
class CrystalFeature : Listener
{

    private val cooldowns = mutableMapOf<UUID, Long>()

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        cooldowns.remove(event.player.uniqueId)
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent)
    {
        val player = event.player
        val item = event.item ?: return
        val game = GameService.byPlayer(player) ?: return

        if (event.action !in listOf(Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR)) return
        if (item.type != Material.SKULL_ITEM) return

        if (!game.state(GameState.Playing)) {
            player.sendMessage("${CC.RED}You cannot place crystals right now!")
            return
        }

        event.isCancelled = true

        val cooldownTime = cooldowns.getOrDefault(player.uniqueId, 0L)
        if (System.currentTimeMillis() - cooldownTime <= (0.5 * 1000)) return

        cooldowns[player.uniqueId] = System.currentTimeMillis()
        val clickedBlock = event.clickedBlock ?: return
        if (clickedBlock.type !in listOf(Material.BEDROCK, Material.OBSIDIAN)) return

        val blockLocation = clickedBlock.location.add(0.5, 1.0, 0.5)
        if (clickedBlock.world.getNearbyEntities(clickedBlock.location, 2.0, 2.0, 2.0).any {
                blockLocation.distance(it.location) < 0.7
            }) return

        if (item.amount == 1) {
            player.inventory.itemInHand = null
        } else {
            item.amount -= 1
        }

        player.updateInventory()
        player.world.spawnEntity(blockLocation, EntityType.ENDER_CRYSTAL)
    }

    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        if (event.entityType == EntityType.ENDER_CRYSTAL && event.entity.world.name == "spawn") {
            event.isCancelled = true
        }
    }
}