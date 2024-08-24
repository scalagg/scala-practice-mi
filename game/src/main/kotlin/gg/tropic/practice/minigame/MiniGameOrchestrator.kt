package gg.tropic.practice.minigame

import gg.tropic.practice.games.GameImpl

/**
 * @author GrowlyX
 * @since 8/19/2024
 */
interface MiniGameOrchestrator<T : MiniGameConfiguration>
{
    fun id(): String
    fun prepare(game: GameImpl, configuration: T): MiniGameLifecycle<T>
}
