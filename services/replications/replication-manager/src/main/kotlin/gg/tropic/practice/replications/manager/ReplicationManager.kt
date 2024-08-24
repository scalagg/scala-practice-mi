package gg.tropic.practice.replications.manager

import com.github.benmanes.caffeine.cache.Caffeine
import gg.scala.aware.thread.AwareThreadContext
import gg.tropic.practice.application.api.DPSRedisService
import gg.tropic.practice.application.api.DPSRedisShared
import gg.tropic.practice.expectation.GameExpectation
import gg.tropic.practice.namespace
import gg.tropic.practice.replication.ReplicationResult
import gg.tropic.practice.replication.ReplicationResultStatus
import gg.tropic.practice.suffixWhenDev
import net.evilblock.cubed.serializers.Serializers
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 9/24/2023
 */
object ReplicationManager
{
    private val redis = DPSRedisService("replicationmanager-inhibitor")
        .apply(DPSRedisService::start)

    private val gameInstanceCache = Caffeine.newBuilder()
        .expireAfterWrite(2L, TimeUnit.SECONDS)
        .removalListener<String, String> { _, _, _ -> syncStatusIndexes() }
        .build<String, String>()

    private val dpsCache = DPSRedisShared.keyValueCache

    data class AllServerStatuses(
        val indexes: Map<String, String>
    )

    fun allServerStatuses() = gameInstanceCache
        .asMap().mapValues { (_, value) ->
            dpsCache
                .sync()
                .get(value)
                .let {
                    Serializers.gson.fromJson(
                        it, ReplicationStatus::class.java
                    )
                }
        }

    private fun syncStatusIndexes()
    {
        ForkJoinPool.commonPool().submit {
            dpsCache.sync().set(
                "${namespace().suffixWhenDev()}:replicationmanager:status-indexes",
                Serializers.gson.toJson(
                    AllServerStatuses(gameInstanceCache.asMap())
                )
            )
        }
    }

    fun load()
    {
        val service = Executors.newSingleThreadScheduledExecutor()
        service.scheduleAtFixedRate({
            replicationCallbacks.entries.toList()
                .filter { it.value.first + 5000L <= System.currentTimeMillis() }
                .forEach {
                    it.value.second.complete(
                        ReplicationResult(
                            status = ReplicationResultStatus.Unavailable,
                            message = "Service did not get response in time"
                        )
                    )

                    replicationCallbacks.remove(it.key)
                }
        }, 0L, 200L, TimeUnit.MILLISECONDS)

        redis.configure {
            listen("status") {
                val server = retrieve<String>("server")
                val status = retrieve<ReplicationStatus>("status")

                val key = "${namespace().suffixWhenDev()}:replicationmanager:status:$server"

                dpsCache.sync().psetex(
                    key, 1000 * 2,
                    Serializers.gson.toJson(status)
                )

                gameInstanceCache.put(server, key)
                syncStatusIndexes()
            }

            listen("replication-ready") {
                val requestID = retrieve<UUID>("requestID")
                val result = retrieve<ReplicationResult>("result")

                replicationCallbacks[requestID]?.second
                    ?.complete(result)
            }
        }
    }

    private val replicationCallbacks = ConcurrentHashMap<UUID, Pair<Long, CompletableFuture<ReplicationResult>>>()

    fun allocateReplication(
        server: String,
        map: String,
        expectation: GameExpectation
    ): CompletableFuture<ReplicationResult>
    {
        val future = CompletableFuture<ReplicationResult>()
        val requestID = UUID.randomUUID()
        redis.createMessage(
            "allocate-replication",
            "requestID" to requestID,
            // TODO: jesus christ
            "expectation" to String(
                Base64.getEncoder().encode(
                    Serializers.gson.toJson(expectation)
                        .encodeToByteArray()
                )
            ),
            "map" to map,
            "server" to server
        ).publish(
            AwareThreadContext.SYNC,
            channel = "practice:replicationmanager-inhabitants".suffixWhenDev()
        )

        replicationCallbacks[requestID] = System.currentTimeMillis() to future
        return future
    }

    fun requestReplication(
        server: String,
        map: String,
        expectation: GameExpectation
    ): CompletableFuture<ReplicationResult>
    {
        val future = CompletableFuture<ReplicationResult>()
        val requestID = UUID.randomUUID()
        redis.createMessage(
            "request-replication",
            "requestID" to requestID,
            // TODO: jesus christ
            "expectation" to String(
                Base64.getEncoder().encode(
                    Serializers.gson.toJson(expectation)
                        .encodeToByteArray()
                )
            ),
            "map" to map,
            "server" to server
        ).publish(
            AwareThreadContext.SYNC,
            channel = "practice:replicationmanager-inhabitants".suffixWhenDev()
        )

        replicationCallbacks[requestID] = System.currentTimeMillis() to future
        return future
    }
}
