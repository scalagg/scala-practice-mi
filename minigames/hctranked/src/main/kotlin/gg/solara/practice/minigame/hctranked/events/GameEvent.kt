package gg.solara.practice.minigame.hctranked.events

import gg.solara.practice.minigame.hctranked.HCTRankedLifecycle
import net.evilblock.cubed.util.time.TimeUtil
import java.time.Duration
import kotlin.math.max

/**
 * @author Topu
 * @date 10/2/2024
 */
abstract class GameEvent(
    val lifecycle: HCTRankedLifecycle,
    private val name: String,
    val durationPostGameStart: Duration
): Runnable
{
    var start: Long = 0L

    fun describe() = "$name ${
        if (isNow()) "NOW!" else TimeUtil.formatIntoMMSS((millisUntilExecution() / 1000L).toInt())
    }"

    fun formattedEstimatedExecution() = TimeUtil.formatIntoMMSS((millisUntilExecution() / 1000L).toInt())
    fun isNow() = estimatedExecution() - System.currentTimeMillis() < 0
    fun millisUntilExecution() = max(0, estimatedExecution() - System.currentTimeMillis())
    fun estimatedExecution() = durationPostGameStart
        .plusMillis(start)
        .toMillis()
}
