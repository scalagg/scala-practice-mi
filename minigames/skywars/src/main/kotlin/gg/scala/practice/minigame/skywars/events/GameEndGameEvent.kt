package gg.scala.practice.minigame.skywars.events

import gg.scala.practice.minigame.skywars.SkyWarsLifecycle
import java.time.Duration

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
class GameEndGameEvent(lifecycle: SkyWarsLifecycle) : GameEvent(
    lifecycle, "Game End", Duration.ofSeconds(90)
)
{
    override fun run()
    {
        lifecycle.game.complete(null)
    }
}
