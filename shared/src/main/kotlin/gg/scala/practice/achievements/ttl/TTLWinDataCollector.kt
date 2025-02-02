package gg.scala.practice.achievements.ttl

import gg.scala.achievements.plugin.model.goals.ttl.TTLRespectingGoalDataCollector
import org.joda.time.DateTime

/**
 * @author Topu
 * @date 2/2/2025
 */
class TTLWinDataCollector(
    override val goalId: String,
    override var value: Int = 0,
    override var lastTtlHeartbeat: Long = DateTime
        .now()
        .millis
) : TTLRespectingGoalDataCollector<Int>
{
    override fun defaultValueOf() = 0
    override fun getAbstractType() = TTLWinDataCollector::class.java

    override fun nextExpectedTtlRefresh(): DateTime =
        DateTime(lastTtlHeartbeat)
            .plusDays(1)
}