package gg.tropic.practice.minigame

import gg.tropic.practice.games.GameImpl
import me.lucko.helper.terminable.composite.CompositeTerminable

/**
 * @author GrowlyX
 * @since 8/23/2024
 */
interface MiniGameLifecycle<T : MiniGameConfiguration> : CompositeTerminable
{
    val configuration: T
    val game: GameImpl
    val scoreboard: MiniGameScoreboard

    fun configure()
}
