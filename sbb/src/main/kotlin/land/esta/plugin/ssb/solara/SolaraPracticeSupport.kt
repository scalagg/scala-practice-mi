package land.esta.plugin.ssb.solara

import gg.tropic.practice.games.robot.RobotGameLifecycle.createNewRobotInstances
import gg.tropic.practice.games.robot.RobotInstance
import gg.tropic.practice.namespace
import land.esta.plugin.ssb.bot.impl.SolaraPracticeRobotInstanceImpl
import net.evilblock.cubed.ScalaCommonsSpigot

object SolaraPracticeSupport
{
    fun enable()
    {
        createNewRobotInstances = context@{ game ->
            val leader = game.humanSide().players.first()
            val settingProfile = loadSettingsFromSelectionSync(
                leader,
                game.botGameMetadata!!.botNamespace,
                game.botGameMetadata!!.botProfileID
            )

            if (game.botGameMetadata!!.botInstances.size == 1)
            {
                return@context setOf(SolaraPracticeRobotInstanceImpl(game, settingProfile, 1))
            }

            val instances = mutableSetOf<RobotInstance>()
            repeat(game.botGameMetadata!!.botInstances.size) {
                instances += SolaraPracticeRobotInstanceImpl(game, settingProfile, it + 1)
            }

            return@context instances
        }
    }
}
