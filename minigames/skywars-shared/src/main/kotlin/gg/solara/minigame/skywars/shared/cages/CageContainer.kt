package gg.solara.minigame.skywars.shared.cages

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
data class CageContainer(
    val cages: MutableMap<String, Cage> = mutableMapOf()
)
