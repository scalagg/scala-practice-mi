package gg.solara.practice.minigame.skywars.chests

import gg.solara.practice.minigame.skywars.SkyWarsLifecycle
import gg.tropic.practice.map.metadata.anonymous.Position
import gg.tropic.practice.map.metadata.anonymous.toPosition
import org.bukkit.block.Block

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
class ChestController(private val game: SkyWarsLifecycle)
{
    val chests = mutableMapOf<String, List<Position>>()

    fun chestByBlock(block: Block) = chests.values.flatten()
        .filter {
            it.isEqualToVector(block.location.toPosition())
        }

    fun chestByScope(scope: String = "global") = chests[scope] ?: listOf()
}
