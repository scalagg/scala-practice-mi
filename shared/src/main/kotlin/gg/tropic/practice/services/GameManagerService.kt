package gg.tropic.practice.services

import gg.scala.aware.AwareBuilder
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.tropic.practice.games.GameStatus
import gg.tropic.practice.games.GameStatusIndexes
import gg.tropic.practice.kit.KitService
import gg.tropic.practice.namespace
import gg.tropic.practice.suffixWhenDev
import me.lucko.helper.Schedulers
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.serializers.Serializers
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 9/25/2023
 */
@Service(priority = 1600)
object GameManagerService
{
    @Inject
    lateinit var plugin: ExtendedScalaPlugin

    private val aware by lazy {
        AwareBuilder
            .of<AwareMessage>("practice:gamemanager".suffixWhenDev())
            .codec(AwareMessageCodec)
            .logger(Logger.getAnonymousLogger())
            .build()
    }

    private val metadataTrackers = ConcurrentHashMap<String, GenericMetadataTracker>()
    interface GenericMetadataTracker
    {
        fun update(): CompletableFuture<Void>
    }

    data class QueueIDMetadataTracker(
        val queueId: String,
        var inQueue: Int = 0,
        var inGame: Int = 0,
    ) : GenericMetadataTracker
    {
        fun integerValueOf(key: String) = ScalaCommonsSpigot
            .instance
            .kvConnection
            .sync()
            .get(
                "${namespace().suffixWhenDev()}:metadata:$key:$queueId"
            )
            ?.toIntOrNull()
            ?: 0

        override fun update() = CompletableFuture
            .runAsync {
                inGame = integerValueOf("users-playing")
                inQueue = integerValueOf("users-queued")
            }
    }

    fun buildQueueIdMetadataTracker(queueId: String): QueueIDMetadataTracker
    {
        if (metadataTrackers.containsKey(queueId))
        {
            return metadataTrackers[queueId]!! as QueueIDMetadataTracker
        }

        metadataTrackers[queueId] = QueueIDMetadataTracker(queueId)
        return metadataTrackers[queueId]!! as QueueIDMetadataTracker
    }

    data class AggregateQueueMetadataTracker(
        val id: String,
        var inQueue: Int = 0,
        var inGame: Int = 0,
    ) : GenericMetadataTracker
    {
        fun integerValueOf(key: String, queueId: String) = ScalaCommonsSpigot
            .instance
            .kvConnection
            .sync()
            .get(
                "${namespace().suffixWhenDev()}:metadata:$key:$queueId"
            )
            ?.toIntOrNull()
            ?: 0

        override fun update() = CompletableFuture
            .runAsync {
                val queueIDs = KitService.cached().kits.keys
                    .map { kit ->
                        "$kit${this.id.removePrefix("aggregate")}"
                    }

                inGame = queueIDs.sumOf { integerValueOf("users-playing", it) }
                inQueue =queueIDs.sumOf { integerValueOf("users-queued", it) }
            }
    }

    fun buildAggregateMetadataTracker(queueId: String): AggregateQueueMetadataTracker
    {
        val aggregateQueueID = "aggregate:$queueId"
        if (metadataTrackers.containsKey(aggregateQueueID))
        {
            return metadataTrackers[aggregateQueueID]!! as AggregateQueueMetadataTracker
        }

        metadataTrackers[aggregateQueueID] = AggregateQueueMetadataTracker(aggregateQueueID)
        return metadataTrackers[aggregateQueueID]!! as AggregateQueueMetadataTracker
    }

    private fun createMessage(packet: String, vararg pairs: Pair<String, Any?>): AwareMessage =
        AwareMessage.of(packet, aware, *pairs)

    fun bindToMetadataService(): ScheduledExecutorService
    {
        val service = Executors.newSingleThreadScheduledExecutor()
        service.scheduleAtFixedRate({
            CompletableFuture
                .allOf(
                    *metadataTrackers.values
                        .map(GenericMetadataTracker::update)
                        .toTypedArray()
                )
                .join()
        }, 0L, 100L, TimeUnit.MILLISECONDS)
        return service
    }

    fun bindToStatusService(statusService: () -> GameStatus)
    {
        Schedulers
            .async()
            .runRepeating(Runnable {
                createMessage(
                    packet = "status",
                    "server" to ServerSync.local.id,
                    "status" to Serializers.gson
                        .toJson(statusService())
                ).publish(
                    context = AwareThreadContext.SYNC
                )
            }, 0L, 5L)
            .bindWith(plugin)

        plugin.logger.info("Bound status service. Status updates for available games will be pushed to the gamemanager channel ever 0.25 seconds.")
    }

    fun allGames() = allGameStatuses()
        .thenApply {
            it.values
                .flatMap(GameStatus::games)
        }

    fun allGameStatuses() = CompletableFuture
        .supplyAsync {
            ScalaCommonsSpigot.instance.kvConnection
                .sync()
                .get("${namespace().suffixWhenDev()}:gamemanager:status-indexes")
                .let {
                    Serializers.gson.fromJson(
                        it, GameStatusIndexes::class.java
                    )
                }
                .indexes
                .mapValues { (_, value) ->
                    ScalaCommonsSpigot.instance.kvConnection
                        .sync()
                        .get(value)
                        .let {
                            Serializers.gson.fromJson(
                                it, GameStatus::class.java
                            )
                        }
                }
        }

    @Configure
    fun configure()
    {
        aware.connect().toCompletableFuture().join()
    }
}
