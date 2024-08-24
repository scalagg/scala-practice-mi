package gg.tropic.practice.queue.skywars

import com.google.common.annotations.Beta
import gg.tropic.practice.application.api.DPSRedisShared
import gg.tropic.practice.application.api.defaults.map.MapDataSync
import gg.tropic.practice.expectation.GameExpectation
import gg.tropic.practice.games.manager.GameManager
import gg.tropic.practice.games.matchmaking.InternalMatchmakingBracket
import gg.tropic.practice.games.matchmaking.MatchmakingMetadata
import gg.tropic.practice.games.team.GameTeam
import gg.tropic.practice.games.team.TeamIdentifier
import gg.tropic.practice.namespace
import gg.tropic.practice.queue.GameQueueManager
import gg.tropic.practice.queue.QueueEntry
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
open class BetaRankedSkyWarsGameQueue : () -> Unit
{
    private var thread: Thread? = null
    private val metadataUpdateThread = Executors
        .newSingleThreadScheduledExecutor()

    fun queueId() = "skywars:ranked"

    open fun start()
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
                psetex("${namespace().suffixWhenDev()}:metadata:users-queued:${
                    queueId()
                }", 1000L, queueSize.toString())

                val gamesMatchingQueueID = GameManager.allGames()
                    .thenApply { refs ->
                        refs.filter { it.queueId == queueId() }
                    }
                    .join()
                val playersInGame = gamesMatchingQueueID.sumOf { it.players.size }

                psetex("${namespace().suffixWhenDev()}:metadata:users-playing:${
                    queueId()
                }", 1000L, playersInGame.toString())
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

    fun cleanup()
    {
        GameQueueManager
            .getQueueEntriesFromId(queueId())
            .forEach { (_, value) ->
                GameQueueManager.removeQueueEntryFromId(queueId(), value.leader)
                GameQueueManager.destroyQueueStates(
                    queueID = queueId(),
                    entry = value
                )

                DPSRedisShared.sendMessage(
                    value.players,
                    listOf(
                        "&c&lDue to a reboot of one of our systems, you have been removed from the queue you were in."
                    )
                )
            }

        Logger.getGlobal()
            .info(
                "[queue] Cleanup procedure for queue ${queueId()} completed."
            )
    }

    fun destroy()
    {
        checkNotNull(thread)
        thread?.interrupt()
        thread = null

        metadataUpdateThread.shutdownNow()
    }

    protected open fun run()
    {
        val length = GameQueueManager.queueSizeFromId(queueId())
        if (length > 0)
        {
            Thread.sleep(200)
            return
        }

        val queueEntries = GameQueueManager.getQueueEntriesFromId(queueId())
        val existingGames

        val group = groupedQueueEntries.random()
            .onEach {
                runCatching {
                    GameQueueManager.removeQueueEntryFromId(queueId(), it.leader)
                }.onFailure {
                    it.printStackTrace()
                }
            }

        // Expecting a symmetric list here, lets hope it doesn't break?
        first = group.take(gameSize)
        second = group.takeLast(gameSize)

        if (first.size != gameSize || second.size != gameSize)
        {
            Thread.sleep(200)
            return
        }

        val firstPlayers = first.flatMap { it.players }
        val secondPlayers = second.flatMap { it.players }

        val users = listOf(
            firstPlayers,
            secondPlayers
        ).flatten()

        val map = MapDataSync
            .selectRandomMapCompatibleWith(kit)
            ?: return run {
                DPSRedisShared.sendMessage(
                    users,
                    listOf(
                        "&c&lWe found no map compatible with the kit you are queueing for!"
                    )
                )
            }

        val region = first.first().preferredQueueRegion
        val expectation = GameExpectation(
            identifier = UUID.randomUUID(),
            players = listOf(first, second)
                .flatMap {
                    it.flatMap { entry ->
                        entry.players
                    }
                },
            teams = setOf(
                GameTeam(teamIdentifier = TeamIdentifier.A, players = firstPlayers),
                GameTeam(teamIdentifier = TeamIdentifier.B, players = secondPlayers)
            ),
            kitId = kit.id,
            mapId = map.name,
            queueType = queueType,
            queueId = queueId(),
            matchmakingMetadata = MatchmakingMetadata(
                region = if (region == Region.Both) Region.NA else region,
                bracket = InternalMatchmakingBracket.of(first.first().leaderELO)
            )
        )

        GameQueueManager.prepareGameFor(
            map = map,
            expectation = expectation,
            // prefer NA servers if queuing globally
            region = if (region == Region.Both) Region.NA else region,
            cleanup = {
                for (queueEntry in first + second)
                {
                    GameQueueManager
                        .destroyQueueStates(
                            queueId(), queueEntry
                        )
                }
            }
        ).exceptionally {
            it.printStackTrace()
            return@exceptionally null
        }.join()

        Thread.sleep(200)
    }

    override fun invoke()
    {
        while (true)
        {
            runCatching {
                run()
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}
