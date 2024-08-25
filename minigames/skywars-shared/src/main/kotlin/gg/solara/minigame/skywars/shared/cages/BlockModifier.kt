package gg.solara.minigame.skywars.shared.cages

import org.bukkit.util.Vector

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
data class BlockModifier(
    val relationToBaseBlock: Vector,
    val blockData: String
)
