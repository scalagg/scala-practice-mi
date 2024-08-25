package gg.solara.minigame.skywars.events

import gg.solara.minigame.skywars.SkyWarsLifecycle
import gg.tropic.practice.minigame.MiniGameLifecycle
import net.evilblock.cubed.util.time.TimeUtil
import java.time.Duration
import kotlin.math.max
import kotlin.math.min

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
abstract class GameEvent(
    val lifecycle: SkyWarsLifecycle,
    val name: String,
    val durationPostGameStart: Duration
): Runnable
{
    fun describe() = "$name ${
        if (isNow()) "NOW!" else TimeUtil.formatIntoMMSS((millisUntilExecution() / 1000L).toInt())
    }"

    fun isNow() = estimatedExecution() - System.currentTimeMillis() < 0
    fun millisUntilExecution() = max(0, estimatedExecution() - System.currentTimeMillis())
    fun estimatedExecution() = durationPostGameStart
        .plusMillis(lifecycle.game.startTimestamp)
        .toMillis()
}
