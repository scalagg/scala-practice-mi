package gg.solara.minigame.skywars.events

import java.time.Duration

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
abstract class GameEvent(
    val name: String,
    val durationPostGameStart: Duration
): Runnable
{
    var hasBeenExecuted = false
}
