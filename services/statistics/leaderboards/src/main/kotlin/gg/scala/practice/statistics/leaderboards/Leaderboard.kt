package gg.scala.practice.statistics.leaderboards

import gg.scala.practice.application.api.defaults.kit.ImmutableKit
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 12/16/2023
 */
data class Leaderboard(
    val leaderboardType: LeaderboardType,
    val kit: ImmutableKit?,
    val initial: Boolean = false
)
{
    init
    {
        if (initial)
        {
            Logger.getGlobal().info(
                "[leaderboards] Configured a leaderboard ${leaderboardId()}"
            )
        }
    }

    fun leaderboardId() = "${leaderboardType.name}:${kit?.id ?: "global"}"
}
