package gg.scala.practice.minigame

import gg.scala.practice.games.GameImpl
import me.lucko.helper.terminable.composite.CompositeTerminable

/**
 * @author GrowlyX
 * @since 8/23/2024
 */
interface MiniGameLifecycle<T : MiniGameConfiguration> : CompositeTerminable
{
    val configuration: T
    val game: AbstractMiniGameGameImpl<T>
    val scoreboard: MiniGameScoreboard

    fun configure()
}
