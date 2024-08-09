package land.esta.plugin.ssb.bot.impl

import gg.tropic.practice.games.GameImpl
import gg.tropic.practice.games.robot.RobotInstance
import land.esta.plugin.ssb.bot.setting.setting.RootAccessor
import land.esta.plugin.ssb.botevents.events.BotDeathEvent
import land.esta.plugin.ssb.bots.Bot
import land.esta.plugin.ssb.solara.SolaraPracticeIntegrator
import me.lucko.helper.Events
import net.evilblock.cubed.util.Color
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 7/20/2024
 */
class SolaraPracticeRobotInstanceImpl(private val gameImpl: GameImpl) : RobotInstance()
{
    private var spawnedBot: Bot? = null

    init
    {
        terminable.with {
            if (spawnedBot?.isAlive == false)
            {
                return@with
            }

            // Auto destroy it if the terminable closes, we don't wanna waste resources
            spawnedBot?.silentKill()
        }

        Events
            .subscribe(BotDeathEvent::class.java)
            .filter { it.bot.uniqueId == spawnedBot?.uniqueId }
            .handler {
                gameImpl.complete(
                    gameImpl.humanSide()
                )
            }
            .bindWith(terminable)
    }

    override fun name(): String = spawnedBot?.profile?.botName?.let(Color::translate) ?: ""
    override fun ping() = spawnedBot?.profile?.value(RootAccessor.connectionSettings.ping) ?: 100

    override fun initialSpawn()
    {
        if (spawnedBot == null)
        {
            spawnedBot = SolaraPracticeIntegrator.spawn(
                gameImpl.map
                    .findSpawnLocationMatchingTeam(
                        gameImpl.robotSide().side
                    )!!
                    .toLocation(gameImpl.arenaWorld!!),
                gameImpl
            )
        }
    }

    override fun participantConnected(player: Player)
    {
        spawnedBot?.viewsManager?.addPlayer(player)
    }

    override fun spectatorConnected(player: Player)
    {
        spawnedBot?.viewsManager?.addPlayer(player)
    }

    override fun destroy()
    {
        spawnedBot?.silentKill()
    }
}
