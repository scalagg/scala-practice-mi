package gg.solara.practice.minigame.skywars.shared.cages

import org.bukkit.Material
import org.bukkit.util.Vector

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
data class BlockModifier(
    val relationToBaseBlock: Vector,
    val blockType: Material,
    val blockData: Byte = 0
)
