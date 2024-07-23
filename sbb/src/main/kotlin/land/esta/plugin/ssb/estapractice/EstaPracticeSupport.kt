package land.esta.plugin.ssb.estapractice

import gg.tropic.practice.games.robot.RobotGameLifecycle.createNewRobotInstance
import land.esta.plugin.ssb.bot.impl.EstaPracticeRobotInstanceImpl
import java.util.concurrent.CompletableFuture

object EstaPracticeSupport
{
    fun enable()
    {
        createNewRobotInstance = { game ->
            CompletableFuture.completedFuture(
                EstaPracticeRobotInstanceImpl(game)
            )
        }
    }
}
