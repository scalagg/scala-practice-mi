package land.esta.plugin.ssb.bot.impl

import gg.tropic.practice.games.GameImpl
import gg.tropic.practice.games.robot.RobotInstance
import land.esta.plugin.ssb.bots.Bot
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 7/20/2024
 */
class EstaPracticeRobotInstanceImpl(private val gameImpl: GameImpl, private val spawnedBot: Bot) : RobotInstance()
{
    init
    {
        terminable.with {
            if (spawnedBot.isKilled)
            {
                return@with
            }

            // Auto destroy it if the terminable closes, we don't wanna waste resources
            spawnedBot.silentKill()
        }
    }

    override fun name(): String = spawnedBot.settingManager.botName
    override fun participantConnected(player: Player)
    {
        if (!spawnedBot.hasRegisteredOnlyPlayer)
        {
            spawnedBot.hasRegisteredOnlyPlayer
            spawnedBot.registerPlayer(player)
        }

        spawnedBot.viewsManager.addPlayer(player)
    }

    override fun spectatorConnected(player: Player)
    {
        spawnedBot.viewsManager.addPlayer(player)
    }
}
