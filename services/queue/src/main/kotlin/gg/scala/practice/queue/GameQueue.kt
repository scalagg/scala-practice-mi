package gg.scala.practice.queue

import gg.scala.practice.application.api.DPSRedisShared
import gg.scala.practice.application.api.defaults.kit.ImmutableKit
import gg.scala.practice.application.api.defaults.map.MapDataSync
import gg.scala.practice.expectation.GameExpectation
import gg.scala.practice.games.manager.GameManager
import gg.scala.practice.games.team.GameTeam
import gg.scala.practice.games.team.TeamIdentifier
import gg.scala.practice.namespace
import gg.scala.practice.region.Region
import gg.scala.practice.suffixWhenDev
import net.evilblock.cubed.serializers.Serializers
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import kotlin.concurrent.thread
import kotlin.math.max
import kotlin.math.min

/**
 * @author GrowlyX
 * @since 9/17/2023
 */
open class GameQueue(
    val kit: ImmutableKit,
    val queueType: QueueType,
    private val teamSize: Int
) : () -> Unit {

    companion object {
        @JvmStatic
        val RUN_RANGE_EXPANSION_UPDATES = { time: Long ->
            System.currentTimeMillis() >= time + 1500L
        }
    }

    private var thread: Thread? = null
    private var periodicUpdateThread: Thread? = null
    private val metadataUpdateThread = Executors.newSingleThreadScheduledExecutor()

    open fun queueId() = "${kit.id}:${queueType.name}:${teamSize}v${teamSize}"

    open fun expandRanges() {
        val entries = GameQueueManager.getQueueEntriesFromId(queueId())

        for ((hash, entry) in entries) {
            var requiresUpdates = false

            if (queueType == QueueType.Ranked && RUN_RANGE_EXPANSION_UPDATES(entry.lastELORangeExpansion)) {
                entry.leaderRangedELO.diffsBy = min(2000, (entry.leaderRangedELO.diffsBy * 1.5).toInt())
                entry.lastELORangeExpansion = System.currentTimeMillis()
                requiresUpdates = true
            }

            val previousPingDiff = entry.leaderRangedPing.diffsBy
            if (entry.maxPingDiff != -1 && entry.leaderRangedPing.diffsBy < entry.maxPingDiff) {
                if (RUN_RANGE_EXPANSION_UPDATES(entry.lastPingRangeExpansion)) {
                    entry.leaderRangedPing.diffsBy = min(entry.maxPingDiff, (entry.leaderRangedPing.diffsBy * 1.5).toInt())
                    entry.lastPingRangeExpansion = System.currentTimeMillis()
                    entry.lastRecordedDifferential = entry.leaderRangedPing.diffsBy - previousPingDiff
                    requiresUpdates = true
                }
            }

            if (entry.preferredQueueRegion != Region.Both && System.currentTimeMillis() - entry.joinQueueTimestamp >= 10_000L) {
                requiresUpdates = true
                entry.preferredQueueRegion = Region.Both
            }

            if (requiresUpdates) {
                GameQueueManager.useCache {
                    hset("${namespace().suffixWhenDev()}:queues:${queueId()}:entries", hash, Serializers.gson.toJson(entry))
                }
            }
        }
    }

    fun expandRangeBlock() {
        while (!Thread.currentThread().isInterrupted) {
            runCatching { expandRanges() }.onFailure { it.printStackTrace() }
            Thread.sleep(1000L)
        }
    }

    open fun start() {
        check(thread == null)
        thread = thread(isDaemon = true, name = "queues-${queueId()}", block = this)
        periodicUpdateThread = thread(isDaemon = true, name = "queue-periodic-${queueId()}", block = ::expandRangeBlock)

        metadataUpdateThread.scheduleAtFixedRate({
            val queueSize = GameQueueManager.queueSizeFromId(queueId())
            DPSRedisShared.keyValueCache.sync().psetex("${namespace().suffixWhenDev()}:metadata:users-queued:${queueId()}", 1000L, queueSize.toString())
        }, 0L, 50L, TimeUnit.MILLISECONDS)
    }

    fun cleanup() {
        GameQueueManager.getQueueEntriesFromId(queueId()).forEach { (_, value) ->
            GameQueueManager.removeQueueEntryFromId(queueId(), value.leader)
        }
    }

    fun destroy() {
        thread?.interrupt()
        periodicUpdateThread?.interrupt()
        metadataUpdateThread.shutdownNow()
        thread = null
        periodicUpdateThread = null
    }

    protected open fun run() {
        if (GameQueueManager.queueSizeFromId(queueId()) < teamSize * 2) {
            Thread.sleep(200)
            return
        }

        val queueEntries = GameQueueManager.getQueueEntriesFromId(queueId()).values.toList()
        val groupedQueueEntries = queueEntries.mapNotNull { entry ->
            val matches = queueEntries.filter { otherEntry ->
                entry != otherEntry && (entry.preferredQueueRegion == otherEntry.preferredQueueRegion)
            }
            if (matches.size >= teamSize * 2) matches + entry else null
        }

        if (groupedQueueEntries.isEmpty()) {
            Thread.sleep(200)
            return
        }

        val group = groupedQueueEntries.random().onEach {
            runCatching { GameQueueManager.removeQueueEntryFromId(queueId(), it.leader) }.onFailure { it.printStackTrace() }
        }

        val first = group.take(teamSize)
        val second = group.takeLast(teamSize)

        val internalCleanup = {
            for (queueEntry in first + second) {
                GameQueueManager.destroyQueueStates(queueId(), queueEntry)
            }
        }

        if (first.size != teamSize || second.size != teamSize) {
            internalCleanup()
            Thread.sleep(200)
            return
        }

        val firstPlayers = first.flatMap { it.players }
        val secondPlayers = second.flatMap { it.players }
        val users = (firstPlayers + secondPlayers).toSet()

        val map = MapDataSync.selectRandomMapCompatibleWith(kit) ?: run {
            internalCleanup()
            return
        }

        val expectation = GameExpectation(
            identifier = UUID.randomUUID(),
            players = users.toMutableSet(),
            teams = setOf(
                GameTeam(TeamIdentifier.A, firstPlayers.toMutableSet()),
                GameTeam(TeamIdentifier.B, secondPlayers.toMutableSet())
            ),
            kitId = kit.id,
            mapId = map.name,
            queueType = queueType,
            queueId = queueId()
        )

        val region = first.first().preferredQueueRegion
        GameQueueManager.prepareGameFor(
            map = map,
            expectation = expectation,
            region = if (region == Region.Both) Region.NA else region,
            cleanup = internalCleanup
        ).exceptionally {
            it.printStackTrace()
            null
        }.join()

        Thread.sleep(200)
    }

    override fun invoke() {
        while (!Thread.currentThread().isInterrupted) {
            runCatching { run() }.onFailure { it.printStackTrace() }
        }
    }
}

