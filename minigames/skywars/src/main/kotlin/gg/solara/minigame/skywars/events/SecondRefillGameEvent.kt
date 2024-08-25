package gg.solara.minigame.skywars.events

import gg.solara.minigame.skywars.SkyWarsLifecycle
import java.time.Duration

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
class SecondRefillGameEvent(lifecycle: SkyWarsLifecycle) : GameEvent(
    lifecycle, "Refill", Duration.ofSeconds(145 + 80)
)
{
    override fun run()
    {
        // TODO
    }
}
