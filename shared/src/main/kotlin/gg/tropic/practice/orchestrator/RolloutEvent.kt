package gg.tropic.practice.orchestrator

import gg.tropic.practice.region.Region
import java.text.SimpleDateFormat
import java.util.Date

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
data class RolloutEvent(
    val region: Region,
    val affected: Set<String>,
    val requiringRollout: MutableSet<String> = mutableSetOf(),
    val completedRollout: MutableSet<String> = mutableSetOf(),
    var status: RolloutStatus = RolloutStatus.InProgress,
    val started: Long = System.currentTimeMillis()
)
{
    fun describe() = arrayOf(
        "Region: $region",
        "",
        "Affected: $affected",
        "Rollout In-Progress (awaiting): $requiringRollout",
        "Completed: $completedRollout",
        "",
        "Started: ${SimpleDateFormat().format(Date(started))}"
    )
}
