package gg.solara.plugin.robots.solara

import gg.tropic.practice.games.GameState
import gg.tropic.practice.games.robot.RobotGameLifecycle.createNewRobotInstances
import gg.tropic.practice.games.robot.RobotInstance
import gg.solara.plugin.robots.bot.impl.SolaraPracticeRobotInstanceImpl
import gg.solara.plugin.robots.bots.events.impl.BotDeathEvent
import me.lucko.helper.Events
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
                    if (totalDeaths.incrementAndGet() == game.consumedBots.get())
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
