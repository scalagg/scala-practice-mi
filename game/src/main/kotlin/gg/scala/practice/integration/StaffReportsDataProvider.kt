package gg.scala.practice.integration

import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.scala.staff.reports.ReportProcessor
import gg.scala.practice.games.GameService
import gg.scala.practice.queue.QueueType
import net.evilblock.cubed.util.CC
import org.bukkit.Bukkit

/**
 * @author GrowlyX
 * @since 1/16/2024
 */
@Service
@IgnoreAutoScan
@SoftDependency("ScStaff")
object StaffReportsDataProvider
{
    @Configure
    fun configure()
    {
        ReportProcessor.provideAdditionalServerData { _, uuid ->
            val bukkitPlayer = Bukkit.getPlayer(uuid)
                ?: return@provideAdditionalServerData mapOf()

            val gameOfPlayer = GameService.byPlayer(bukkitPlayer)
                ?: return@provideAdditionalServerData mapOf()

            return@provideAdditionalServerData mapOf(
                "Kit" to gameOfPlayer.kit.displayName,
                "Bot Fight" to if (gameOfPlayer.robot())
                    "${CC.GREEN}Yes" else "${CC.RED}No",
                "Ranked" to if (gameOfPlayer.expectationModel.queueType == QueueType.Ranked)
                    "${CC.GREEN}Yes" else "${CC.RED}No"
            )
        }
    }
}
