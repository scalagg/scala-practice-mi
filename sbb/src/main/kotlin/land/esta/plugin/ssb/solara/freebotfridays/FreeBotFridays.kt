package land.esta.plugin.ssb.solara.freebotfridays

import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.time.TimeUtil
import java.time.*

/**
 * @author GrowlyX
 * @since 8/9/2024
 */
object FreeBotFridays
{
    private val newYorkLocalEST = ZoneId.of("America/New_York")

    private val fbfStart = LocalTime.of(15, 30) // 3:30PM EST
    private val fbfEnd = LocalTime.of(19, 30) // 7:30PM EST

    fun isFreeBotFriday(): Boolean
    {
        val now = ZonedDateTime.now(newYorkLocalEST)
        val currentTime = now.toLocalTime()

        return now.dayOfWeek == DayOfWeek.FRIDAY &&
            currentTime.isAfter(fbfStart) &&
            currentTime.isBefore(fbfEnd)
    }

    fun timeUntilNextFreeBotFriday(): Duration
    {
        val now = ZonedDateTime.now(newYorkLocalEST)

        val nextFriday = now.withDayOfWeek(DayOfWeek.FRIDAY).with(fbfStart)
        val targetDateTime = if (now.dayOfWeek == DayOfWeek.FRIDAY && now.toLocalTime().isAfter(fbfStart))
        {
            nextFriday.plusWeeks(1)
        } else
        {
            nextFriday
        }

        // Calculate the duration between now and the target date and time
        val duration = Duration.between(now, targetDateTime)
        return duration
    }

    fun ZonedDateTime.withDayOfWeek(dayOfWeek: DayOfWeek): ZonedDateTime
    {
        var result = this.with(dayOfWeek)
        if (result.isBefore(this))
        {
            result = result.plusWeeks(1)
        }
        return result
    }

    fun fancyStatus(): String
    {
        if (isFreeBotFriday())
        {
            return "${CC.GOLD}NOW!"
        }

        return "${CC.GREEN}${TimeUtil.formatIntoAbbreviatedString(
            timeUntilNextFreeBotFriday()
                .toSeconds()
                .toInt()
        )}"
    }
}
