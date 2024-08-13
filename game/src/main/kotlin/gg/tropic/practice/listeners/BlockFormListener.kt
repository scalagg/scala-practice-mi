package gg.tropic.practice.listeners

import gg.scala.commons.annotations.Listeners
import gg.tropic.practice.games.GameService
import gg.tropic.practice.games.GameService.plugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockFormEvent
import org.bukkit.metadata.FixedMetadataValue

/**
 * @author TastyCake
 * @date 8/13/2024
 */
class BlockFormListener : Listener
{
    @EventHandler
    fun onForm(event: BlockFormEvent) {
        val n = event.newState.type
        if (n == Material.OBSIDIAN
            || n == Material.STONE
            || n == Material.COBBLESTONE) {

            event.newState.setMetadata(
                "placed",
                FixedMetadataValue(plugin, true)
            )

            for (player in Bukkit.getOnlinePlayers()) {
                if (player.location.distance(event.newState.location) <= 100) {
                    val game = GameService.byPlayer(player) ?: continue
                    game.placedBlocks += event.newState.location.toVector()
                }
            }
        }
    }
}