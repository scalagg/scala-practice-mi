package gg.solara.practice.minigame.skywars.shared.cages

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
data class SchemaCageContainer(
    val schemas: MutableMap<String, SchemaCage> = mutableMapOf()
)
