package gg.solara.practice.minigame.hctranked.events

import gg.solara.practice.minigame.hctranked.HCTRankedLifecycle
import java.time.Duration

/**
 * @author Topu
 * @date 10/2/2024
 */
class GameEndGameEvent(lifecycle: HCTRankedLifecycle) : GameEvent(
    lifecycle, "Game End", Duration.ofSeconds(90)
)
{
    override fun run()
    {
        lifecycle.game.complete(null)
    }
}