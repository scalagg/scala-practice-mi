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
import gg.tropic.practice.expectation.GameExpectation
import gg.tropic.practice.map.Map
import gg.tropic.practice.map.MapService
import gg.tropic.practice.replication.ReplicationResult
import gg.tropic.practice.replication.ReplicationResultStatus
import gg.tropic.practice.replications.models.ReplicationStatus
import gg.tropic.practice.suffixWhenDev
import me.lucko.helper.Schedulers
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.serializers.Serializers
import java.util.Base64
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 9/24/2023
 */
@Service(priority = 1600)
object ReplicationManagerService : CompositeTerminable by CompositeTerminable.create()
{
    @Inject
    lateinit var plugin: ExtendedScalaPlugin

    private val aware by lazy {
        AwareBuilder
            .of<AwareMessage>("practice:replicationmanager-inhabitants".suffixWhenDev())
            .codec(AwareMessageCodec)
            .logger(Logger.getAnonymousLogger())
            .build()
    }

    private fun createMessage(packet: String, vararg pairs: Pair<String, Any?>): AwareMessage =
        AwareMessage.of(packet, aware, *pairs)

    fun bindToStatusService(statusService: () -> ReplicationStatus)
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
                    context = AwareThreadContext.SYNC,
                    channel = "practice:replicationmanager-inhibitor".suffixWhenDev()
                )
            }, 0L, 10L)
            .bindWith(this)

        plugin.logger.info("Bound status service. Status updates for available replications will be pushed to the replicationmanager channel ever 0.5 seconds.")
    }

    var buildNewReplication: (Map, GameExpectation) -> CompletableFuture<ReplicationResult> = { _, _ ->
        CompletableFuture.completedFuture(null)
    }

    var allocateExistingReplication: (Map, GameExpectation) -> CompletableFuture<ReplicationResult> = { _, _ ->
        CompletableFuture.completedFuture(null)
    }

    @Configure
    fun configure()
    {
        fun AwareMessage.prepareReplication(
            future: (Map, GameExpectation) -> CompletableFuture<ReplicationResult>
        )
        {
            val requestID = retrieve<UUID>("requestID")
            val server = retrieve<String>("server")
            if (ServerSync.local.id != server)
            {
                return
            }

            val map = MapService
                .mapWithID(retrieve<String>("map"))
                ?: return run {
                    createMessage(
                        "replication-ready",
                        "requestID" to requestID,
                        "result" to Serializers.gson.toJson(ReplicationResult(
                            status = ReplicationResultStatus.Unavailable,
                            message = "Map service did not find the map you were looking for"
                        ))
                    ).publish(
                        AwareThreadContext.SYNC,
                        channel = "practice:replicationmanager-inhibitor".suffixWhenDev()
                    )
                }

            val expectation = Serializers.gson.fromJson(
                String(
                    Base64.getDecoder().decode(
                        retrieve<String>("expectation")
                    )
                ),
                GameExpectation::class.java
            )

            future(map, expectation)
                .thenAccept {
                    createMessage(
                        "replication-ready",
                        "requestID" to requestID,
                        "result" to Serializers.gson.toJson(it)
                    ).publish(
                        AwareThreadContext.SYNC,
                        channel = "practice:replicationmanager-inhibitor".suffixWhenDev()
                    )
                }
                .exceptionally {
                    it.printStackTrace()
                    createMessage(
                        "replication-ready",
                        "requestID" to requestID,
                        "result" to Serializers.gson.toJson(ReplicationResult(
                            status = ReplicationResultStatus.Unavailable,
                            message = "System threw an internal error"
                        ))
                    ).publish(
                        AwareThreadContext.SYNC,
                        channel = "practice:replicationmanager-inhibitor".suffixWhenDev()
                    )
                    return@exceptionally null
                }
        }

        aware.listen("allocate-replication") {
            prepareReplication { map, expectation ->
                allocateExistingReplication(map, expectation)
            }
        }
        aware.listen("request-replication") {
            prepareReplication { map, expectation ->
                buildNewReplication(map, expectation)
            }
        }

        aware.connect().toCompletableFuture().join()
    }
}
