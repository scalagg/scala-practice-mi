package gg.scala.practice.statistics.leaderboards

import gg.scala.practice.application.api.defaults.kit.ImmutableKit
import gg.scala.practice.statistics.KitStatistics
import gg.scala.practice.statistics.bot.BotKitStatistics
import gg.scala.practice.statistics.ranked.RankedKitStatistics

/**
 * @author GrowlyX
 * @since 9/25/2023
 */
enum class LeaderboardType(
    val fromKit: ImmutablePracticeProfile.(ImmutableKit) -> Long?,
    val fromGlobal: ImmutablePracticeProfile.() -> Long?,
    val enforceRanked: Boolean = false
)
{
    ELO(
        fromKit = {
            rankedStatistics[it.id]?.elo?.toLong()
        },
        fromGlobal = {
            val average = rankedStatistics.values
                .map(RankedKitStatistics::elo)
                .average()

            if (average.isNaN()) null else average.toLong()
        },
        enforceRanked = true
    ),
    UnrankedWins(
        fromKit = {
            unrankedStatistics[it.id]?.wins?.toLong()
        },
        fromGlobal = {
            unrankedStatistics.values
                .sumOf(KitStatistics::wins)
                .toLong()
        }
    ),
    RankedWins(
        fromKit = {
            rankedStatistics[it.id]?.wins?.toLong()
        },
        fromGlobal = {
            rankedStatistics.values
                .sumOf(KitStatistics::wins)
                .toLong()
        }
    ),
    UnrankedWinStreak(
        fromKit = context@{
            val volatile = unrankedStatistics[it.id]?.dailyStreak
                ?: return@context null

            volatile().toLong()
        },
        fromGlobal = {
            val result = unrankedStatistics.maxOfOrNull {
                val volatile = unrankedStatistics[it.key]?.dailyStreak
                    ?: return@maxOfOrNull -1L

                volatile().toLong()
            }

            if (result == -1L) null else result
        }
    ),
    RankedWinStreak(
        fromKit = context@{
            val volatile = rankedStatistics[it.id]?.dailyStreak
                ?: return@context null

            volatile().toLong()
        },
        fromGlobal = {
            val result = rankedStatistics.maxOfOrNull {
                val volatile = rankedStatistics[it.key]?.dailyStreak
                    ?: return@maxOfOrNull -1L

                volatile().toLong()
            }

            if (result == -1L) null else result
        },
        enforceRanked = true
    )
}
