package gg.solara.practice.minigame.hctranked

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.solara.practice.minigame.hctranked.shared.HCTRankedGameConfiguration
import gg.tropic.practice.games.GameService
import gg.tropic.practice.games.GameState
import gg.tropic.practice.games.event.GameStartEvent
import gg.tropic.practice.minigame.AbstractMiniGameGameImpl
import gg.tropic.practice.minigame.BasicMiniGameOrchestrator
import gg.tropic.practice.minigame.broadcastSeconds
import gg.tropic.practice.minigame.event.MiniGameStartTickEvent
import me.lucko.helper.Events
import net.evilblock.cubed.util.CC
import org.bukkit.Sound
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * @author Topu
 * @date 10/2/2024
 */
@Service
object HCTRankedGameOrchestrator : BasicMiniGameOrchestrator<HCTRankedGameConfiguration>() {

    override fun prepare(
        miniGame: AbstractMiniGameGameImpl<HCTRankedGameConfiguration>,
        configuration: HCTRankedGameConfiguration
    ) = HCTRankedLifecycle(configuration, miniGame)

    @Configure
    fun configure()
    {
        Events
            .subscribe(MiniGameStartTickEvent::class.java)
            .filter { it.game.miniGameLifecycle is HCTRankedLifecycle }
            .filter { it.secondsLeftUntilStart in broadcastSeconds }
            .handler {
                it.game.playSound(Sound.NOTE_STICKS, 1.0f)
                it.game.sendMessage("${CC.YELLOW}The match starts in ${CC.RED}${it.secondsLeftUntilStart}${CC.YELLOW} second${
                    if (it.secondsLeftUntilStart == 1) "" else "s"
                }!")
            }

        Events
            .subscribe(PlayerQuitEvent::class.java)
            .handler {
                val game = GameService.byPlayer(it.player)
                    ?: return@handler

                if (game.state(GameState.Waiting) || game.state(GameState.Playing)) {
                    val hctRanked = game.miniGameLifecycle as HCTRankedLifecycle
                    hctRanked.game.preWaitRemove(it.player)
                    it.player.sendMessage("${CC.RED}You have left the match!")
                }
            }

        Events
            .subscribe(BlockBreakEvent::class.java)
            .handler {
                val game = GameService.byPlayer(it.player)
                    ?: return@handler

                val miniGameLifecycle = game.miniGameLifecycle
                if (miniGameLifecycle is HCTRankedLifecycle) {
                    it.isCancelled = true
                    it.player.sendMessage("${CC.RED}You can't break blocks in HCTRanked!")
                }
            }

        Events
            .subscribe(PlayerInteractEvent::class.java)
            .filter { it.clickedBlock != null && it.action == Action.RIGHT_CLICK_BLOCK }
            .handler {
                val game = GameService.byPlayer(it.player)
                    ?: return@handler

                if (game.state(GameState.Waiting) || game.state(GameState.Starting)) {
                    it.isCancelled = true
                    return@handler
                }

                val miniGameLifecycle = game.miniGameLifecycle
                if (miniGameLifecycle is HCTRankedLifecycle) {
                    //TODO: Interactions
                }
            }

        Events
            .subscribe(GameStartEvent::class.java)
            .filter { it.game.miniGameLifecycle is HCTRankedLifecycle }
            .handler {
                it.game.playSound(Sound.ENDERDRAGON_GROWL, 1.0f)
                it.game.sendMessage("${CC.YELLOW}The battle begins! Good luck!")
            }
    }

    override val id = "hctranked"
    override val minimumPlayersRequiredToEnterStarting = 4
    override val startGameCountDown = 5
}