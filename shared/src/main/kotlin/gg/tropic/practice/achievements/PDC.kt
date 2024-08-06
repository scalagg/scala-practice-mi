package gg.tropic.practice.achievements

import gg.scala.achievements.plugin.model.goals.AchievementGoalDataCollector

/**
 * @author GrowlyX
 * @since 8/3/2024
 */
class PDC(
    override val goalId: String,
    override var value: Int = 0,
) : AchievementGoalDataCollector<Int>
{
    override fun getAbstractType() =
        PDC::class.java
}
