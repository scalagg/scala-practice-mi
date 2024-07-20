package gg.tropic.practice.queue

import gg.tropic.practice.Globals
import gg.tropic.practice.application.api.DPSRedisShared
import gg.tropic.practice.application.api.defaults.game.GameExpectation
import gg.tropic.practice.application.api.defaults.game.GameTeam
import gg.tropic.practice.application.api.defaults.game.GameTeamSide
import gg.tropic.practice.application.api.defaults.kit.ImmutableKit
import gg.tropic.practice.application.api.defaults.map.MapDataSync
import gg.tropic.practice.region.Region
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
        val first = queueEntries.values.first()

        // a bot is playing w/ the player lol
        val users = first.players + Globals.PLAYER_BOT_UNIQUE_ID

        val map = MapDataSync
            .selectRandomMapCompatibleWith(kit)
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
                GameTeamSide.B to GameTeam(side = GameTeamSide.B, players = listOf(Globals.PLAYER_BOT_UNIQUE_ID)),
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
