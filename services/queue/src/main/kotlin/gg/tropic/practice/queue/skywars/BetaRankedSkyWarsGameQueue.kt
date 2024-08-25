package gg.tropic.practice.queue.skywars

import com.google.common.annotations.Beta
import gg.solara.practice.minigame.skywars.shared.SkyWarsConstants
import gg.solara.practice.minigame.skywars.shared.SkyWarsGameConfiguration
import gg.solara.practice.minigame.skywars.shared.SkyWarsMode
import gg.tropic.practice.application.api.DPSRedisShared
import gg.tropic.practice.application.api.defaults.kit.ImmutableKit
import gg.tropic.practice.application.api.defaults.map.MapDataSync
import gg.tropic.practice.expectation.GameExpectation
import gg.tropic.practice.games.GameState
import gg.tropic.practice.games.manager.GameManager
import gg.tropic.practice.games.matchmaking.InternalMatchmakingBracket
import gg.tropic.practice.games.matchmaking.JoinIntoGameStatus
import gg.tropic.practice.games.matchmaking.MatchmakingMetadata
import gg.tropic.practice.games.team.GameTeam
import gg.tropic.practice.games.team.TeamIdentifier
import gg.tropic.practice.namespace
import gg.tropic.practice.queue.GameQueue
import gg.tropic.practice.queue.GameQueueManager
import gg.tropic.practice.queue.QueueType
import gg.tropic.practice.region.Region
import gg.tropic.practice.suffixWhenDev
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import kotlin.concurrent.thread

/**
 * @author GrowlyX
 * @since 9/17/2023
 */
@Beta
open class BetaRankedSkyWarsGameQueue(kit: ImmutableKit) : GameQueue(kit, QueueType.Ranked, 4)
{
    private var thread: Thread? = null
    private val metadataUpdateThread = Executors
        .newSingleThreadScheduledExecutor()

    override fun queueId() = SkyWarsConstants.QID
    override fun start()
    {
        check(thread == null)
        Logger.getGlobal()
            .info(
                "[queue] Building a queue with id: ${queueId()}"
            )

        thread = thread(
            isDaemon = true,
            name = "queues-${queueId()}",
            block = this
        )

        Logger.getGlobal()
            .info(
                "[queue] Started queue iterator"
            )

        metadataUpdateThread.scheduleAtFixedRate({
            with(DPSRedisShared.keyValueCache.sync()) {
                val queueSize = GameQueueManager.queueSizeFromId(queueId())
                psetex(
                    "${namespace().suffixWhenDev()}:metadata:users-queued:${
                        queueId()
                    }", 1000L, queueSize.toString()
                )

                val gamesMatchingQueueID = GameManager.allGames()
                    .thenApply { refs ->
                        refs.filter { it.queueId == queueId() }
                    }
                    .join()
                val playersInGame = gamesMatchingQueueID.sumOf { it.players.size }

                psetex(
                    "${namespace().suffixWhenDev()}:metadata:users-playing:${
                        queueId()
                    }", 1000L, playersInGame.toString()
                )
            }
        }, 0L, 50L, TimeUnit.MILLISECONDS)

        metadataUpdateThread.scheduleAtFixedRate({
            kotlin.runCatching {
                GameQueueManager.getAllQueuePlayers(queueId())
                    .forEach {
                        val entry = GameQueueManager.getQueueEntryFromId(queueId(), it)
                        if (entry != null)
                        {
                            return@forEach
                        }

                        GameQueueManager
                            .removeQueueEntryFromId(queueId(), UUID.fromString(it))

                        Logger.getGlobal().info("Removing $it as the player does not have a queue entry")
                    }

                GameQueueManager.getQueueEntriesFromId(queueId())
                    .forEach { (t, u) ->
                        if (System.currentTimeMillis() - u.joinQueueTimestamp >= 60_000L)
                        {
                            val isPlayerOnline = DPSRedisShared.keyValueCache.sync()
                                .hexists(
                                    "player:${u.leader}",
                                    "instance"
                                )

                            if (!isPlayerOnline)
                            {
                                GameQueueManager.removeQueueEntryFromId(queueId(), u.leader)
                            }
                        }
                    }
            }.onFailure {
                it.printStackTrace()
            }
        }, 0L, 1L, TimeUnit.SECONDS)

        Logger.getGlobal()
            .info(
                "[queue] Built queue with ID: ${queueId()}"
            )
    }

    override fun run()
    {
        val length = GameQueueManager.queueSizeFromId(queueId())
        if (length > 0)
        {
            Thread.sleep(200)
            return
        }

        val targetEntry = GameQueueManager.getQueueEntriesFromId(queueId())
            .values.firstOrNull()
            ?: return

        GameQueueManager.removeQueueEntryFromId(queueId(), targetEntry.leader)

        val preferredRegion = if (targetEntry.preferredQueueRegion == Region.Both)
            Region.NA else targetEntry.preferredQueueRegion

        val matchmakingMetadata = MatchmakingMetadata(
            region = preferredRegion,
            bracket = InternalMatchmakingBracket.of(targetEntry.leaderELO)
        )

        val existingGameRequiringPlayers = GameManager.allGames().join()
            .filter { it.queueId == queueId() }
            .filter { it.state == GameState.Waiting }
            .firstOrNull {
                it.metadata != null &&
                    it.metadata!!.isCompatible(matchmakingMetadata)
            }

        val map = MapDataSync
            .selectRandomMapCompatibleWith(kit)
            ?: return run {
                GameQueueManager.destroyQueueStates(queueId(), targetEntry)
                DPSRedisShared.sendMessage(
                    listOf(targetEntry.leader),
                    listOf(
                        "&c&lWe found no map compatible with the kit you are queueing for!"
                    )
                )
            }

        if (existingGameRequiringPlayers != null)
        {
            GameQueueManager
                .requestJoinIntoGame(targetEntry.leader, existingGameRequiringPlayers)
                .thenAccept {
                    if (it.status == JoinIntoGameStatus.Success)
                    {
                        DPSRedisShared.redirect(
                            listOf(targetEntry.leader),
                            existingGameRequiringPlayers.server
                        )
                    }
                }
                .thenRun {
                    GameQueueManager.destroyQueueStates(queueId(), targetEntry)
                }
                .exceptionally {
                    it.printStackTrace()
                    return@exceptionally null
                }
                .join()
        } else
        {
            val expectation = GameExpectation(
                identifier = UUID.randomUUID(),
                players = mutableSetOf(targetEntry.leader),
                teams = setOf(
                    GameTeam(teamIdentifier = TeamIdentifier.A, players = mutableSetOf(targetEntry.leader)),
                    GameTeam(teamIdentifier = TeamIdentifier.B, players = mutableSetOf()),
                    GameTeam(teamIdentifier = TeamIdentifier.C, players = mutableSetOf()),
                    GameTeam(teamIdentifier = TeamIdentifier.D, players = mutableSetOf())
                ),
                kitId = kit.id,
                mapId = map.name,
                queueType = queueType,
                queueId = queueId(),
                miniGameConfiguration = SkyWarsGameConfiguration(
                    mode = SkyWarsMode.Ranked
                ),
                matchmakingMetadata = MatchmakingMetadata(
                    region = preferredRegion,
                    bracket = InternalMatchmakingBracket.of(targetEntry.leaderELO)
                )
            )

            GameQueueManager
                .prepareGameFor(
                    map = map,
                    expectation = expectation,
                    // prefer NA servers if queuing globally
                    region = preferredRegion,
                    cleanup = {
                        GameQueueManager.destroyQueueStates(queueId(), targetEntry)
                    }
                )
                .exceptionally {
                    it.printStackTrace()
                    return@exceptionally null
                }
                .join()
        }

        Thread.sleep(200)
    }
}
