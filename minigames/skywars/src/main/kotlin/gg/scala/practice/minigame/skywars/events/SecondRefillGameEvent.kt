package gg.scala.practice.minigame.skywars.events

import gg.scala.practice.minigame.skywars.SkyWarsLifecycle
import java.time.Duration

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
class SecondRefillGameEvent(lifecycle: SkyWarsLifecycle) : GameEvent(
    lifecycle, "Refill", Duration.ofSeconds(80)
), ChestRefillEvent
{
    override fun run()
    {
        lifecycle.chestController.refillAll()
        runEffects(lifecycle.game.arenaWorld!!)
    }
}
