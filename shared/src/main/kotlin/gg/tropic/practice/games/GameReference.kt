package gg.tropic.practice.games

import gg.tropic.practice.games.matchmaking.MatchmakingMetadata
import java.util.*

/**
 * @author GrowlyX
 * @since 9/25/2023
 */
data class GameReference(
    val uniqueId: UUID,
    val mapID: String,
    val kitID: String,
    val state: GameState,
    val replicationID: String,
    val server: String,
    val players: List<UUID>,
    val spectators: List<UUID>,
    val majorityAllowsSpectators: Boolean,
    val queueId: String? = null,
    val metadata: MatchmakingMetadata? = null
)
