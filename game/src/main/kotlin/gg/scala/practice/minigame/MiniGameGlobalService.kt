package gg.scala.practice.minigame

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.util.QuickAccess
import gg.scala.practice.PracticeGame
import gg.scala.practice.games.GameState
import gg.scala.practice.games.event.PlayerJoinGameEvent
import gg.scala.practice.minigame.event.PlayerMiniGameQuitWhileStartingEvent
import me.lucko.helper.Events
import net.evilblock.cubed.util.CC

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
@Service
object MiniGameGlobalService
{
    @Inject
    lateinit var plugin: PracticeGame

    @Configure
    fun configure()
    {
        Events
            .subscribe(PlayerMiniGameQuitWhileStartingEvent::class.java)
            .handler {
                it.game.preWaitRemove(it.player)
                it.game.sendMessage("${QuickAccess.coloredName(it.player)}${CC.YELLOW} has left!")
            }
            .bindWith(plugin)

        Events
            .subscribe(PlayerJoinGameEvent::class.java)
            .filter { it.game is AbstractMiniGameGameImpl<*> }
            .handler {
                if (it.game.state(GameState.Waiting) || it.game.state(GameState.Starting))
                {
                    val miniGameOrchestrator = (it.game as AbstractMiniGameGameImpl<*>).orchestrator
                    it.game.sendMessage("${QuickAccess.coloredName(it.player)}${CC.YELLOW} has joined ${CC.YELLOW}(${CC.AQUA}${
                        it.game.expectationModel.players.size
                    }${CC.YELLOW}/${CC.AQUA}${
                        miniGameOrchestrator.minimumPlayersRequiredToEnterStarting
                    }${CC.YELLOW})!")
                }
            }
            .bindWith(plugin)
    }
}
