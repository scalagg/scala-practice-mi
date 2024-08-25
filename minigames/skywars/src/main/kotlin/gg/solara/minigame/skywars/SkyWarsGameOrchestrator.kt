package gg.solara.minigame.skywars

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.solara.minigame.skywars.shared.SkyWarsGameConfiguration
import gg.tropic.practice.games.event.GameStartEvent
import gg.tropic.practice.minigame.AbstractMiniGameGameImpl
import gg.tropic.practice.minigame.BasicMiniGameOrchestrator
import gg.tropic.practice.minigame.broadcastSeconds
import gg.tropic.practice.minigame.event.MiniGameStartTickEvent
import me.lucko.helper.Events
import net.evilblock.cubed.util.CC
import org.bukkit.Sound

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
@Service
object SkyWarsGameOrchestrator : BasicMiniGameOrchestrator<SkyWarsGameConfiguration>()
{
    override fun prepare(
        miniGame: AbstractMiniGameGameImpl<SkyWarsGameConfiguration>,
        configuration: SkyWarsGameConfiguration
    ) = SkyWarsLifecycle(configuration, miniGame)

    @Configure
    fun configure()
    {
        /**
         * Global subscriptions
         */
        Events
            .subscribe(MiniGameStartTickEvent::class.java)
            .filter { it.game.miniGameLifecycle is SkyWarsLifecycle }
            .filter { it.secondsLeftUntilStart in broadcastSeconds }
            .handler {
                it.game.playSound(Sound.NOTE_STICKS, 1.0f)
                it.game.sendMessage("${CC.YELLOW}Cages open in ${CC.RED}${it.secondsLeftUntilStart}${CC.YELLOW} second${
                    if (it.secondsLeftUntilStart == 1) "" else "s"
                }!")
            }

        Events
            .subscribe(GameStartEvent::class.java)
            .filter { it.game.miniGameLifecycle is SkyWarsLifecycle }
            .handler {
                it.game.playSound(Sound.ENDERDRAGON_GROWL, 1.0f)
                it.game.sendMessage("${CC.YELLOW}Cages opened! ${CC.RED}FIGHT!")
            }
    }

    override val id = "skywars"
    override val minimumPlayersRequiredToEnterStarting = 4
}
