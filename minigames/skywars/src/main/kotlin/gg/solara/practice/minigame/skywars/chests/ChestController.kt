package gg.solara.practice.minigame.skywars.chests

import gg.solara.practice.minigame.skywars.SkyWarsLifecycle
import gg.solara.practice.minigame.skywars.loot.StandardLootGenerator
import gg.tropic.practice.map.metadata.anonymous.Position
import gg.tropic.practice.map.metadata.anonymous.toPosition
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

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

    fun Inventory.populateSmart(items: List<ItemStack>)
    {
        val newContents = arrayOfNulls<ItemStack>(27)
        for ((index, item) in items.withIndex())
        {
            if (Random.nextBoolean())
            {
                newContents[index] = item
            }
        }

        newContents.shuffle()
        contents = newContents
    }

    fun refillAll()
    {
        val gameWorld = game.game.arenaWorld
            ?: return

        chests["global"]?.onEach {
            val position = it.toLocation(gameWorld)
            if (position.block.state is Chest)
            {
                val inventory = (position.block.state as Chest).inventory
                inventory.populateSmart(StandardLootGenerator.INSTANCE.middle())
            }
        }

        chests.entries
            .filterNot { it.key == "global" }
            .onEach {
                val islandLoot = StandardLootGenerator.INSTANCE.basic().chunked(27)
                it.value.forEachIndexed { index, chest ->
                    val lootForChest = islandLoot.getOrNull(index)
                        ?: listOf()
                    val position = chest.toLocation(gameWorld)

                    val inventory = (position.block.state as Chest).inventory
                    inventory.populateSmart(lootForChest)
                }
            }
    }
}
