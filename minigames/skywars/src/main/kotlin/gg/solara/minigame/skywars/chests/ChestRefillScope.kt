package gg.solara.minigame.skywars.chests

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
sealed interface ChestRefillScope
{
    data object Island : ChestRefillScope
    {

    }
}
