package gg.tropic.practice.minigame

/**
 * @author GrowlyX
 * @since 8/19/2024
 */
interface MiniGame
{
    fun id(): String
    fun scoreboard(): MiniGameScoreboard
}
