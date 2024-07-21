package land.esta.plugin.ssb.bot.impl

import gg.tropic.practice.games.GameImpl
import gg.tropic.practice.games.robot.RobotInstance
import land.esta.plugin.ssb.botevents.events.BotDeathEvent
import land.esta.plugin.ssb.bots.Bot
import land.esta.plugin.ssb.support.estapractice.EstaPracticeManager
import me.lucko.helper.Events
import net.evilblock.cubed.util.Color
import net.evilblock.cubed.util.bukkit.Tasks
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
            .filter { it.bot.uniqueId == spawnedBot?.uniqueId }
            .handler {
                gameImpl.complete(
                    gameImpl.humanSide()
                )
            }
            .bindWith(terminable)
    }

    override fun name(): String = spawnedBot?.settingManager?.botName?.let(Color::translate) ?: ""
    override fun participantConnected(player: Player)
    {
        if (spawnedBot == null)
        {
            spawnedBot = practiceManager.spawn(
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
        Tasks.delayed(1L) {
            spawnedBot?.viewsManager?.addPlayer(player)
        }
    }
}
