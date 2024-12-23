package gg.scala.practice.achievements

import com.cryptomorin.xseries.XMaterial
import gg.scala.achievements.plugin.ScalaAchievementsPlugin
import gg.scala.achievements.plugin.model.GameMode
import gg.scala.achievements.plugin.service.AchievementService
import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan

/**
 * @author GrowlyX
 * @since 5/22/2023
 */
@Service
@IgnoreAutoScan
@SoftDependency("Achievements")
object PracticeAchievementService
{
    @Configure
    fun configure()
    {
        ScalaAchievementsPlugin.gamemode = GameMode(
            "Duels", XMaterial.LIME_DYE
        )

        /*AchievementService.registerAchievement(
            AchievementRegistry.firstBlood
        )*/
    }
}
