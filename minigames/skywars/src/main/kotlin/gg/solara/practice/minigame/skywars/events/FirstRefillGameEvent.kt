package gg.solara.practice.minigame.skywars.events

import gg.solara.practice.minigame.skywars.SkyWarsLifecycle
import java.time.Duration

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
class FirstRefillGameEvent(lifecycle: SkyWarsLifecycle) : GameEvent(
    lifecycle, "Refill", Duration.ofSeconds(145)
), ChestRefillEvent
{
    override fun run()
    {
        runEffects(lifecycle.game.arenaWorld!!)
    }
}
