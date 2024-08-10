package land.esta.plugin.ssb.solara

import gg.tropic.practice.games.GameState
import gg.tropic.practice.games.robot.RobotGameLifecycle.createNewRobotInstances
import gg.tropic.practice.games.robot.RobotInstance
import gg.tropic.practice.namespace
import land.esta.plugin.ssb.bot.impl.SolaraPracticeRobotInstanceImpl
import land.esta.plugin.ssb.botevents.events.BotDeathEvent
import me.lucko.helper.Events
import net.evilblock.cubed.ScalaCommonsSpigot
import java.util.concurrent.atomic.AtomicInteger

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

            val totalDeaths = AtomicInteger(0)

            Events
                .subscribe(BotDeathEvent::class.java)
                .filter { event ->
                    game.state(GameState.Playing) && game.robotInstance
                        .any { it.uniqueID() == event.bot.uniqueId }
                }
                .handler {
                    if (totalDeaths.get() == game.consumedBots.get())
                    {
                        game.complete(game.humanSide())
                    }
                }
                .bindWith(game)

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
