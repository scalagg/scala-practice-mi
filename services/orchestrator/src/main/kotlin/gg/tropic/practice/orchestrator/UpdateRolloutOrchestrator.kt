package gg.tropic.practice.orchestrator

import gg.scala.aware.thread.AwareThreadContext
import gg.scala.commons.agnostic.sync.server.AbstractServer
import gg.scala.commons.agnostic.sync.server.ServerContainer
import gg.tropic.practice.application.api.DPSRedisService
import gg.tropic.practice.application.api.DPSRedisShared
import gg.tropic.practice.region.Region
import gg.tropic.practice.suffixWhenDev
import net.evilblock.cubed.serializers.Serializers
import java.util.UUID
import java.util.concurrent.locks.ReentrantLock
import java.util.logging.Logger
import kotlin.concurrent.withLock

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
object UpdateRolloutOrchestrator : Thread()
{
    private val lock = ReentrantLock()
    private val events = mutableListOf<RolloutEvent>()
    private val dpsOrchestratorRedis = DPSRedisService("orchestrator")
        .apply(DPSRedisService::start)

    fun load()
    {
        dpsOrchestratorRedis.configure {
            listen("unblock") {
                val server = retrieve<String>("server")
                rolloutUnBlock(server)
            }

            listen("end") {
                val initiator = retrieve<UUID>("player")
                val region = retrieve<Region>("region")

                val similarEvent = events.firstOrNull { it.region == region && it.status == RolloutStatus.InProgress }
                if (similarEvent == null)
                {
                    DPSRedisShared.sendMessage(listOf(initiator), listOf(
                        "&cThere is no similar event in-progress."
                    ))
                    return@listen
                }

                lock.withLock {
                    similarEvent.status = RolloutStatus.Complete
                }

                dpsOrchestratorRedis.createMessage(
                    "rollout-cancel",
                    "servers" to Serializers.gson.toJson(similarEvent.affected)
                ).publish(
                    AwareThreadContext.SYNC,
                    channel = "practice:orchestrator-inhabitants".suffixWhenDev()
                )

                DPSRedisShared.sendMessage(listOf(initiator), listOf(
                    "&aEnded the rollout event for region $region."
                ))
            }

            listen("start") {
                val initiator = retrieve<UUID>("player")
                val region = retrieve<Region>("region")

                if (region == Region.Both)
                {
                    DPSRedisShared.sendMessage(listOf(initiator), listOf(
                        "&cCannot use region \"Both\"! This will make rollout synchronization weird."
                    ))
                    return@listen
                }

                val similarEvent = events.firstOrNull { it.region == region && it.status == RolloutStatus.InProgress }
                if (similarEvent != null)
                {
                    DPSRedisShared.sendMessage(listOf(initiator), listOf(
                        "&cA similar rollout is already in-progress.",
                        *similarEvent.describe()
                    ))
                    return@listen
                }

                val affectedServers = ServerContainer.getServersInGroup("mipgame")
                    .map(AbstractServer::id)
                    .toSet()

                if (affectedServers.size <= 1)
                {
                    DPSRedisShared.sendMessage(listOf(initiator), listOf(
                        "&cA rollout cannot happen with ${affectedServers.size} server(s)."
                    ))
                    return@listen
                }

                val newEvent = RolloutEvent(
                    region = region,
                    affected = ServerContainer.getServersInGroup("mipgame")
                        .filter { it.region.name == region.name }
                        .map(AbstractServer::id)
                        .toSet()
                )

                lock.withLock {
                    events += newEvent
                }

                DPSRedisShared.sendMessage(listOf(initiator), listOf(
                    "&aStarting rollout:",
                    *newEvent.describe()
                ))
            }
        }

        start()
    }

    fun rolloutUnBlock(server: String) = lock.withLock {
        val event = events
            .firstOrNull {
                it.status == RolloutStatus.InProgress &&
                    server in it.requiringRollout
            }
            ?: return

        event.requiringRollout -= server
        event.completedRollout += server
    }

    fun isRolloutBlocked(server: String) = events.any {
        it.status == RolloutStatus.InProgress &&
            server in it.requiringRollout
    }

    private fun runRolloutEvents()
    {
        lock.withLock {
            val inProgress = events.filter { it.status == RolloutStatus.InProgress }
            if (inProgress.isEmpty())
            {
                return@withLock
            }

            for (rollout in inProgress)
            {
                if (rollout.requiringRollout.isNotEmpty())
                {
                    Logger.getGlobal().info("Waiting for ${rollout.requiringRollout} to run rollout...")
                    continue
                }

                val pending = rollout.affected.filterNot { it in rollout.completedRollout }
                if (pending.isEmpty())
                {
                    rollout.status = RolloutStatus.Complete
                    Logger.getGlobal().info("Rollout for region ${rollout.region} is complete")
                    return@withLock
                }

                val firstServer = pending.first()
                rollout.requiringRollout += firstServer
                dpsOrchestratorRedis.createMessage(
                    "request-rollout",
                    "server" to firstServer
                ).publish(
                    AwareThreadContext.SYNC,
                    channel = "practice:orchestrator-inhabitants".suffixWhenDev()
                )

                Logger.getGlobal().info("Requesting rollout for ${pending.first()}. (${pending.size - 1} remaining after)")
            }
        }
    }

    override fun run()
    {
        while (true)
        {
            runCatching {
                runRolloutEvents()
            }.onFailure {
                it.printStackTrace()
            }

            sleep(1000L)
        }
    }
}
