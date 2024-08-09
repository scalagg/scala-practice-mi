package land.esta.plugin.ssb.solara

import gg.tropic.practice.games.robot.RobotGameLifecycle.createNewRobotInstance
import land.esta.plugin.ssb.bot.impl.SolaraPracticeRobotInstanceImpl
import java.util.concurrent.CompletableFuture

object SolaraPracticeSupport
{
    fun enable()
    {
        createNewRobotInstance = { game ->
            CompletableFuture.completedFuture(
                SolaraPracticeRobotInstanceImpl(game)
            )
        }
    }
}
