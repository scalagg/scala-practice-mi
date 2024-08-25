package gg.solara.minigame.skywars.infrastructure.cages

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
data class SchemaCageContainer(
    val schemas: MutableMap<String, SchemaCage> = mutableMapOf()
)
