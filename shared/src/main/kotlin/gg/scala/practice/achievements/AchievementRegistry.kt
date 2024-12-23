package gg.scala.practice.achievements

import gg.scala.achievements.plugin.model.Achievement
import gg.scala.achievements.plugin.model.AchievementCategory
import gg.scala.achievements.plugin.model.AchievementReward
import gg.scala.achievements.plugin.model.AchievementStage
import gg.scala.achievements.plugin.model.goals.AchievementGoal
import gg.scala.achievements.plugin.profile.AchievementProfile
import gg.scala.game.extensions.profile.CorePlayerProfileService
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/3/2024
 */
object AchievementRegistry
{
    fun Int.coinsReward() = object : AchievementReward
    {
        override val description = "${this@coinsReward} coins"

        override fun applyTo(player: Player)
        {
            CorePlayerProfileService
                .find(player)
                ?.addCoins(
                    this@coinsReward,
                    "Achievement reward"
                )
        }
    }

    fun Int.killsGoal() = object : AchievementGoal<Int>
    {
        override val description = "${this@killsGoal} Kill"
        override val id = "kills"
        override val requirement = this@killsGoal

        override fun createDataCollector(profile: AchievementProfile) = PDC("kills")
    }

    val firstBlood = Achievement(
        id = "duels:first-blood",
        name = "First Blood",
        category = AchievementCategory.GameSpecific,
        stages = linkedSetOf(
            AchievementStage(
                description = listOf(
                    "Kill a player in any Duels game!"
                ),
                goals = listOf(1.killsGoal()),
                rewards = listOf(100.coinsReward())
            )
        ),
        rewardsPlayer = true,
        visibleWithinMenu = true
    )
}
