package gg.scala.practice.listeners

import gg.scala.practice.games.GameService
import gg.scala.practice.games.GameService.plugin
import me.lucko.helper.Schedulers
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.metadata.FixedMetadataValue

/**
 * @author TastyCake
 * @date 8/13/2024
 */
class BlockFormListener : Listener
{
    @EventHandler
    fun onForm(event: BlockFromToEvent)
    {
        Schedulers.sync()
            .runLater({
                val formedBlock = event.toBlock.type

                if (formedBlock == Material.OBSIDIAN
                    || formedBlock == Material.STONE
                    || formedBlock == Material.COBBLESTONE
                )
                {
                    event.toBlock.setMetadata(
                        "placed",
                        FixedMetadataValue(plugin, true)
                    )

                    val game = GameService.byWorld(event.block.world)
                        ?: return@runLater
                    game.placedBlocks += event.toBlock.location.toVector()
                }
            }, 10L)
    }
}
