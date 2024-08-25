package gg.solara.practice.minigame.skywars

import com.comphenix.packetwrapper.WrapperPlayServerBlockAction
import com.comphenix.protocol.PacketType
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.solara.practice.minigame.skywars.shared.SkyWarsGameConfiguration
import gg.solara.practice.minigame.skywars.shared.cages.SchemaCageDefaultSchemas
import gg.tropic.practice.games.GameService
import gg.tropic.practice.games.GameState
import gg.tropic.practice.games.event.AsyncPlayerPreJoinGameEvent
import gg.tropic.practice.games.event.GameStartEvent
import gg.tropic.practice.games.event.PlayerJoinGameEvent
import gg.tropic.practice.games.event.PlayerSelectSpawnLocationEvent
import gg.tropic.practice.minigame.AbstractMiniGameGameImpl
import gg.tropic.practice.minigame.BasicMiniGameOrchestrator
import gg.tropic.practice.minigame.broadcastSeconds
import gg.tropic.practice.minigame.event.MiniGameStartTickEvent
import me.lucko.helper.Events
import me.lucko.helper.protocol.Protocol
import net.evilblock.cubed.util.CC
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.event.player.PlayerInteractEvent

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
            .subscribe(PlayerSelectSpawnLocationEvent::class.java)
            .filter { it.game.miniGameLifecycle is SkyWarsLifecycle }
            .filter { !it.spectator }
            .filter { it.game.state(GameState.Waiting) || it.game.state(GameState.Starting) }
            .handler {
                val baseBlock = it.location.world
                    .getBlockAt(it.location)
                    .getRelative(BlockFace.DOWN)

                // TODO: choose proper one based on cosmetics
                it.player.sendMessage("Applying cage")
                SchemaCageDefaultSchemas.DEFAULT.applyTo(baseBlock)
            }

        /**
         * Prevent close action from being sent, unless it's from the server
         */
        Protocol
            .subscribe(PacketType.Play.Server.BLOCK_ACTION)
            .handler {
                val blockAction = WrapperPlayServerBlockAction(it.packet)
                println("block action is being sent ${blockAction.byte2} ${blockAction.byte1} ${blockAction.blockType} | ${it.packet.getMeta<Boolean>("programmatically").isPresent}")
                if (blockAction.byte2 == 0 && blockAction.blockType == Material.CHEST && blockAction.byte1 == 0)
                {
                    if (it.packet.getMeta<Boolean>("programmatically").isPresent)
                    {
                        return@handler
                    }

                    it.isCancelled = true
                }
            }

        Events
            .subscribe(PlayerInteractEvent::class.java)
            .filter { it.clickedBlock != null && it.clickedBlock.state.data is org.bukkit.material.Chest }
            .handler {
                val game = GameService.byPlayer(it.player)
                    ?: return@handler

                if (it.player.hasMetadata("spectator"))
                {
                    it.isCancelled = true
                    return@handler
                }

                if (game.state(GameState.Waiting) || game.state(GameState.Starting))
                {
                    it.isCancelled = true
                }
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
    override val startGameCountDown = 5
}
