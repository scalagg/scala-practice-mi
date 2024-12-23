package gg.scala.practice.games.matchmaking

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
data class JoinIntoGameResult(
    val status: JoinIntoGameStatus
)

enum class JoinIntoGameStatus
{
    Success,
    FailureNonEmptyTeams,
    FailureNoResponseReceived,
    FailureStarted
}
