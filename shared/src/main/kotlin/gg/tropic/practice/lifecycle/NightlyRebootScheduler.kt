package gg.tropic.practice.lifecycle

import gg.scala.basics.plugin.shutdown.ShutdownService
import me.lucko.helper.Schedulers
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.time.TimeUtil
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 8/10/2024
 */
object NightlyRebootScheduler
{
    fun scheduleReboot(hour: Int, minute: Int)
    {
        fun millisUntilNextTime(): Long
        {
            val zone = ZoneId.of("America/New_York")
            val now = ZonedDateTime.now(zone)

            val targetTime = LocalTime.of(hour, minute)
            var nextTime = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)

            if (now.toLocalTime().isAfter(targetTime))
            {
                nextTime = nextTime.plusDays(1)
            }

            val duration = Duration.between(now, nextTime)
            return duration.toMillis()
        }

        Tasks.delayed(1L) {
            Logger.getGlobal().info("Server is rebooting in (nightly):")
            Logger.getGlobal().info(TimeUtil.formatIntoAbbreviatedString((millisUntilNextTime() / 1000L).toInt()))
        }

        Schedulers
            .sync()
            .runLater({
                Logger.getGlobal().info("Initiating automated nightly reboot.")
                ShutdownService.initiateShutdown(181)
            }, millisUntilNextTime(), TimeUnit.MILLISECONDS)
    }
}
