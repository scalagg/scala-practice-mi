package land.esta.plugin.ssb.bot.impl

import gg.tropic.practice.games.GameImpl
import gg.tropic.practice.games.robot.RobotInstance
import land.esta.plugin.ssb.botevents.events.BotDeathEvent
import land.esta.plugin.ssb.bots.Bot
import land.esta.plugin.ssb.support.estapractice.EstaPracticeManager
import me.lucko.helper.Events
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 7/20/2024
 */
class EstaPracticeRobotInstanceImpl(private val gameImpl: GameImpl, private val practiceManager: EstaPracticeManager) : RobotInstance()
{
    private var spawnedBot: Bot? = null

    init
    {
        terminable.with {
            if (spawnedBot?.killed == true)
            {
                return@with
            }

            // Auto destroy it if the terminable closes, we don't wanna waste resources
            spawnedBot?.silentKill()
        }

        Events
            .subscribe(BotDeathEvent::class.java)
            .handler {
                gameImpl.complete(
                    gameImpl.humanSide()
                )
            }
            .bindWith(terminable)
    }

    override fun name(): String = spawnedBot?.settingManager?.botName ?: ""
    override fun participantConnected(player: Player)
    {
        if (spawnedBot == null)
        {
            println("Spawned bot")
            practiceManager.spawn(
                gameImpl.map
                    .findSpawnLocationMatchingTeam(
                        gameImpl.robotSide().side
                    )!!
                    .toLocation(gameImpl.arenaWorld!!),
                gameImpl,
                player,
                false,
                false
            )
            return
        }

        spawnedBot?.viewsManager?.addPlayer(player)
    }

    override fun spectatorConnected(player: Player)
    {
        spawnedBot?.viewsManager?.addPlayer(player)
    }
}
