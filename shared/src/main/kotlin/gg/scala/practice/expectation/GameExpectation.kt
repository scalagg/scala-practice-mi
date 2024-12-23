package gg.scala.practice.expectation

import gg.scala.practice.games.duels.PvPConfiguration
import gg.scala.practice.games.matchmaking.MatchmakingMetadata
import gg.scala.practice.queue.QueueType
import gg.scala.practice.games.team.GameTeam
import gg.scala.practice.minigame.MiniGameConfiguration
import java.util.*

/**
 * @author GrowlyX
 * @since 8/4/2022
 */
data class GameExpectation(
    val identifier: UUID,
    val players: MutableSet<UUID>,
    val teams: Set<GameTeam>,
    val kitId: String,
    val mapId: String,
    /**
     * Null queue types mean it is a private duel.
     */
    val queueType: QueueType? = null,
    val queueId: String? = null,
    val configuration: PvPConfiguration? = null,
    val matchmakingMetadata: MatchmakingMetadata? = null,
    val miniGameConfiguration: MiniGameConfiguration? = null,
)
