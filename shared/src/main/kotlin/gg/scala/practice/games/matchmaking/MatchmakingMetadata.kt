package gg.scala.practice.games.matchmaking

import gg.scala.practice.region.Region

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
data class MatchmakingMetadata(
    val region: Region,
    val bracket: InternalMatchmakingBracket
)
{
    fun isCompatible(other: MatchmakingMetadata) = other.region == region && bracket == other.bracket
}
