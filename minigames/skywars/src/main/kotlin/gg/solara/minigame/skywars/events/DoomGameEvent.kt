package gg.solara.minigame.skywars.events

import gg.solara.minigame.skywars.SkyWarsLifecycle
import java.time.Duration

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
class DoomGameEvent(lifecycle: SkyWarsLifecycle) : GameEvent(
    lifecycle, "Doom", Duration.ofSeconds(220)
)
{
    override fun run()
    {
        // every 30s +1 dragon
    }
}
