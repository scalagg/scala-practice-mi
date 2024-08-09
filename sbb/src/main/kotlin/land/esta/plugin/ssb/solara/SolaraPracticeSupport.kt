package land.esta.plugin.ssb.solara

import gg.tropic.practice.games.robot.RobotGameLifecycle.createNewRobotInstances
import land.esta.plugin.ssb.bot.impl.SolaraPracticeRobotInstanceImpl

object SolaraPracticeSupport
{
    fun enable()
    {
        createNewRobotInstances = { game ->
            setOf(
                // TODO: add more bots based on game configuration
                SolaraPracticeRobotInstanceImpl(game)
            )
        }
    }
}
