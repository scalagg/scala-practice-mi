package gg.tropic.practice.lifecycle

import gg.scala.basics.plugin.shutdown.ShutdownService
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.time.TimeUtil
import org.bukkit.event.player.PlayerJoinEvent
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
    private val zone = ZoneId.of("America/New_York")
    fun now(): ZonedDateTime
    {
        return ZonedDateTime.now(zone)
    }

    fun scheduleReboot(hour: Int, minute: Int)
    {
        val now = now()
        val targetTime = LocalTime.of(hour, minute)
        var nextTime = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)

        if (now.toLocalTime().isAfter(targetTime))
        {
            nextTime = nextTime.plusDays(1)
        }

        val duration = Duration.between(now, nextTime)
        val serverInitial = System.currentTimeMillis()

        Tasks.delayed(1L) {
            Logger.getGlobal().info("Server is rebooting in (nightly):")
            Logger.getGlobal().info(TimeUtil.formatIntoAbbreviatedString((duration.toMillis() / 1000L).toInt()))
        }

        Events
            .subscribe(PlayerJoinEvent::class.java)
            .filter { it.player.isOp }
            .handler {
                val newDuration = Duration.between(now, nextTime)

                Tasks.delayed(1L) {
                    it.player.sendMessage("${CC.GREEN}This instance is scheduled for a nightly reboot in ${
                        TimeUtil.formatIntoAbbreviatedString((newDuration.toMillis() / 1000L).toInt())
                    }")
                    it.player.sendMessage("${CC.GRAY}Instance has been online for ${
                        TimeUtil.formatIntoAbbreviatedString(((System.currentTimeMillis() - serverInitial) / 1000L).toInt())
                    }.")
                }
            }

        Schedulers
            .sync()
            .runLater({
                Logger.getGlobal().info("Initiating automated nightly reboot.")
                ShutdownService.initiateShutdown(181)
            }, duration.toMillis(), TimeUnit.MILLISECONDS)
    }
}
