package gg.tropic.practice.minigame

import gg.tropic.practice.games.GameImpl

/**
 * @author GrowlyX
 * @since 8/23/2024
 */
interface MiniGameLifecycle<T : MiniGameConfiguration>
{
    val configuration: T
    val game: GameImpl
    val scoreboard: MiniGameScoreboard

    fun configure()
}
