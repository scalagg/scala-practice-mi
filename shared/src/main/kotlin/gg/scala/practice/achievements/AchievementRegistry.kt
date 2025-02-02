package gg.scala.practice.achievements

import gg.scala.achievements.plugin.model.Achievement
import gg.scala.achievements.plugin.model.AchievementCategory
import gg.scala.achievements.plugin.model.AchievementReward
import gg.scala.achievements.plugin.model.AchievementStage
import gg.scala.achievements.plugin.model.goals.AchievementGoal
import gg.scala.achievements.plugin.profile.AchievementProfile
import gg.scala.game.extensions.profile.CorePlayerProfileService
import gg.scala.practice.achievements.ttl.TTLWinDataCollector
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

    fun Int.buildStageForWins() = AchievementStage(
        description = listOf(
            "Get ${this@buildStageForWins} daily wins."
        ),
        goals = listOf(
            object : AchievementGoal<Int>
            {
                override val description = "Get ${this@buildStageForWins} daily wins."
                override val id = "dailywins"
                override val requirement = this@buildStageForWins

                override fun createDataCollector(profile: AchievementProfile) =
                    TTLWinDataCollector(id)

                override fun matchesRequirement(value: Any?) =
                    ((value as Int?) ?: 0) >= this@buildStageForWins
            }
        ),
        rewards = listOf(
            object : AchievementReward
            {
                override val description = "100 coins"

                override fun applyTo(player: Player) {
                    CorePlayerProfileService.find(player)!!.addCoins(100)
                }
            }
        )
    )

    val matchWinner = Achievement(
        id = "practice:daily:wins",
        name = "Match Winner",
        stages = linkedSetOf(
            15.buildStageForWins(),
            50.buildStageForWins(),
            100.buildStageForWins(),
            150.buildStageForWins()
        ),
        rewardsPlayer = true,
        visibleWithinMenu = true,
        category = AchievementCategory.GameSpecific
    )
}
