package gg.scala.practice.achievements

import com.cryptomorin.xseries.XMaterial
import gg.scala.achievements.plugin.ScalaAchievementsPlugin
import gg.scala.achievements.plugin.model.AchievementReward
import gg.scala.achievements.plugin.model.AchievementStage
import gg.scala.achievements.plugin.model.GameMode
import gg.scala.achievements.plugin.model.goals.AchievementGoal
import gg.scala.achievements.plugin.profile.AchievementProfile
import gg.scala.achievements.plugin.service.AchievementService
import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.scala.game.extensions.profile.CorePlayerProfileService
import gg.scala.practice.achievements.ttl.TTLWinDataCollector
import org.bukkit.entity.Player

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
            "Practice", XMaterial.LIME_DYE
        )

        AchievementService.registerAchievement(
            AchievementRegistry.matchWinner
        )
    }
}
