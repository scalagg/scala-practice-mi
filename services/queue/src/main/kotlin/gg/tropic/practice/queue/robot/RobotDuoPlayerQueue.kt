package gg.tropic.practice.queue.robot

import gg.tropic.practice.Globals
import gg.tropic.practice.application.api.DPSRedisShared
import gg.tropic.practice.application.api.defaults.kit.ImmutableKit
import gg.tropic.practice.application.api.defaults.kit.KitDataSync
import gg.tropic.practice.application.api.defaults.map.MapDataSync
import gg.tropic.practice.expectation.GameExpectation
import gg.tropic.practice.games.bots.extractBotGameMetadata
import gg.tropic.practice.games.team.GameTeam
import gg.tropic.practice.games.team.TeamIdentifier
import gg.tropic.practice.namespace
import gg.tropic.practice.queue.GameQueue
import gg.tropic.practice.queue.GameQueueManager
import gg.tropic.practice.queue.QueueEntry
import gg.tropic.practice.queue.QueueType
import gg.tropic.practice.region.Region
import gg.tropic.practice.serializable.Message
import net.md_5.bungee.api.chat.ClickEvent
import java.util.*

/**
 * @author GrowlyX
 * @since 9/17/2023
 */
open class RobotDuoPlayerQueue(
    kit: ImmutableKit
) : GameQueue(
    kit, QueueType.Robot, 2
)
{
    fun getBotMetdataOfPlayer(player: UUID) = DPSRedisShared.keyValueCache.sync()
        .get(
            "${namespace()}:bot-metadata:$player"
        )
        .extractBotGameMetadata()

    override fun expandRanges()
    {
        val entries = GameQueueManager.getQueueEntriesFromId(queueId())
        for ((_, entry) in entries)
        {
            if (System.currentTimeMillis() - entry.joinQueueTimestamp >= 15_000L)
            {
                val currentBroadcastLastTimestamp = DPSRedisShared.keyValueCache.sync()
                    .hget(
                        "${namespace()}:bot-duos:queue-broadcast",
                        entry.leader.toString()
                    )
                    ?.toLongOrNull()

                if (
                    currentBroadcastLastTimestamp != null &&
                    System.currentTimeMillis() - currentBroadcastLastTimestamp <= 60_000L
                )
                {
                    continue
                }

                val metadata = getBotMetdataOfPlayer(entry.leader)
                DPSRedisShared.keyValueCache.sync()
                    .hset(
                        "${namespace()}:bot-duos:queue-broadcast",
                        entry.leader.toString(),
                        System.currentTimeMillis().toString()
                    )

                val kit = KitDataSync.cached().kits[metadata.kitID]
                DPSRedisShared.sendBroadcast(
                    Message()
                        .withMessage("{primary}&lA 2v2 ${metadata.botNamespace.lowercase().capitalize()} Bot ${
                            kit?.displayName ?: metadata.kitID
                        } game is starting soon! ")
                        .withMessage("&a(Click to Join)")
                        .andHoverOf(
                            "Join the game!"
                        )
                        .andCommandOf(
                            ClickEvent.Action.RUN_COMMAND,
                            "/play ${metadata.botNamespace} ${metadata.kitID}"
                        )
                )
            }
        }
    }

    override fun run()
    {
        val length = GameQueueManager.queueSizeFromId(queueId())
        if (length != 2L)
        {
            Thread.sleep(200)
            return
        }

        // don't unnecessarily load in and map to data class if not needed
        val queueEntries = GameQueueManager.getQueueEntriesFromId(queueId())
        if (queueEntries.values.isEmpty())
        {
            return
        }

        val first: QueueEntry
        val second: QueueEntry

        val potentialGroups = queueEntries.values
            .map { entry ->
                val metadata = getBotMetdataOfPlayer(entry.leader)
                val otherEntriesMatchingEntry = queueEntries.values
                    .filter { otherEntry ->
                        val otherMetadata = getBotMetdataOfPlayer(otherEntry.leader)
                        entry != otherEntry &&
                            // ensure queueing same difficulty
                            otherMetadata.botNamespace == metadata.botNamespace
                    }

                otherEntriesMatchingEntry + entry
            }
            .filter {
                it.size >= 2
            }

        if (potentialGroups.isEmpty())
        {
            Thread.sleep(200)
            return
        }

        val group = potentialGroups.random()
            .onEach {
                runCatching {
                    GameQueueManager.removeQueueEntryFromId(queueId(), it.leader)
                }.onFailure {
                    it.printStackTrace()
                }
            }

        // Expecting a symmetric list here, lets hope it doesn't break?
        first = group.firstOrNull()
            ?: run {
                Thread.sleep(200)
                return
            }

        second = group.lastOrNull()
            ?: run {
                Thread.sleep(200)
                return
            }

        val metadata = getBotMetdataOfPlayer(first.leader)
        val robotPlayers = listOf(
            Globals.POSSIBLE_PLAYER_BOT_UNIQUE_IDS[0],
            Globals.POSSIBLE_PLAYER_BOT_UNIQUE_IDS[1]
        )

        GameQueueManager.removeQueueEntryFromId(queueId(), first.leader)
        GameQueueManager.removeQueueEntryFromId(queueId(), second.leader)

        // a bot is playing w/ the player lol
        val users = first.players + second.players + robotPlayers
        val map = MapDataSync
            .selectMapIfCompatible(
                kit, metadata.mapID
            )
            ?: return run {
                DPSRedisShared.sendMessage(
                    // don't unnecessarily send message targeting the robot
                    first.players,
                    listOf(
                        "&c&lWe found no map compatible with the kit you are queueing for!"
                    )
                )
            }

        val expectation = GameExpectation(
            identifier = UUID.randomUUID(),
            players = users.toMutableList(),
            teams = setOf(
                GameTeam(
                    teamIdentifier = TeamIdentifier.A,
                    players = (first.players + second.players).toMutableList()
                ),
                GameTeam(
                    teamIdentifier = TeamIdentifier.B,
                    players = robotPlayers.toMutableList()
                ),
            ),
            kitId = kit.id,
            mapId = map.name,
            queueType = QueueType.Robot,
            queueId = queueId()
        )

        val region = first.preferredQueueRegion
        GameQueueManager
            .prepareGameFor(
                map = map,
                expectation = expectation,
                // prefer NA servers if queuing globally
                region = if (region == Region.Both) Region.NA else region,
                cleanup = {
                    GameQueueManager.destroyQueueStates(queueId(), first)
                    GameQueueManager.destroyQueueStates(queueId(), second)
                }
            )
            .exceptionally {
                it.printStackTrace()
                return@exceptionally null
            }
            .join()

        Thread.sleep(200)
    }
}
