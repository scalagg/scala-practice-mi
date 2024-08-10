package gg.tropic.practice.queue

import gg.tropic.practice.Globals
import gg.tropic.practice.application.api.DPSRedisShared
import gg.tropic.practice.application.api.defaults.game.GameExpectation
import gg.tropic.practice.application.api.defaults.game.GameTeam
import gg.tropic.practice.application.api.defaults.game.GameTeamSide
import gg.tropic.practice.application.api.defaults.kit.ImmutableKit
import gg.tropic.practice.application.api.defaults.map.MapDataSync
import gg.tropic.practice.games.bots.extractBotGameMetadata
import gg.tropic.practice.namespace
import gg.tropic.practice.region.Region
import net.evilblock.cubed.ScalaCommonsSpigot
import java.util.*

/**
 * @author GrowlyX
 * @since 9/17/2023
 */
open class RobotSinglePlayerQueue(
    kit: ImmutableKit
) : GameQueue(
    kit, QueueType.Robot, 1
)
{

    fun getBotMetdataOfPlayer(player: UUID) = DPSRedisShared.keyValueCache.sync()
        .get(
            "${namespace()}:bot-metadata:$player"
        )
        .extractBotGameMetadata()

    override fun run()
    {
        val length = GameQueueManager.queueSizeFromId(queueId())
        if (length == 0L)
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

        val first = queueEntries.values.first()
        val metadata = getBotMetdataOfPlayer(first.leader)

        GameQueueManager.removeQueueEntryFromId(queueId(), first.leader)

        /**
         * Single player queue only has support for 1 robot at this time.
         */
        val botUniqueId = Globals.POSSIBLE_PLAYER_BOT_UNIQUE_IDS[0]

        // a bot is playing w/ the player lol
        val users = first.players + botUniqueId
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
            players = users,
            teams = mapOf(
                GameTeamSide.A to GameTeam(side = GameTeamSide.A, players = first.players),
                GameTeamSide.B to GameTeam(side = GameTeamSide.B, players = metadata.botInstances.toList()),
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
                    GameQueueManager
                        .destroyQueueStates(
                            queueId(), first
                        )
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
