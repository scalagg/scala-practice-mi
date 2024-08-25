package gg.solara.minigame.skywars.shared.cages

import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.util.Vector
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
data class SchemaCage(
    val id: String,
    val blockModifiers: MutableMap<String, BlockModifier> = mutableMapOf()
)
{
    fun applyTo(baseBlock: Block)
    {
        if (!Bukkit.isPrimaryThread())
        {
            Tasks.sync { applyTo(baseBlock) }
            return
        }

        blockModifiers.values.forEach {
            val newVector = baseBlock.location.toVector().add(it.relationToBaseBlock)
            val blockRelation = baseBlock.location.world.getBlockAt(
                newVector.toLocation(baseBlock.location.world)
            )

            val state = blockRelation.state
            state.type = it.blockType
            state.rawData = it.blockData
            state.update(true, true)
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun addBlockModifier(relationToBaseBlock: Vector, material: Material, data: Byte = 0)
    {
        blockModifiers[Base64.encode(relationToBaseBlock.toString().encodeToByteArray())] = BlockModifier(
            relationToBaseBlock = relationToBaseBlock,
            blockType = material,
            blockData = data
        )
    }

    fun addBlockModifier(relationToBaseBlock: Vector, block: Block)
    {
        addBlockModifier(relationToBaseBlock, block.type, block.data)
    }

    fun base(material: Material, data: Byte = 0) = addBlockModifier(Vector(0, 0, 0), material, data)
    fun pillar(x: Int, z: Int, material: Material, data: Byte = 0)
    {
        repeat(4) {
            addBlockModifier(Vector(x, it, z), material, data)
        }
    }
}
