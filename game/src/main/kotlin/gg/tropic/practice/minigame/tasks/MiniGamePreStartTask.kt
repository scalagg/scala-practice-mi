package gg.tropic.practice.minigame.tasks

import gg.tropic.practice.games.GameState
import gg.tropic.practice.games.event.GameStartEvent
import gg.tropic.practice.minigame.AbstractMiniGameGameImpl
import gg.tropic.practice.minigame.event.MiniGameEnterStartEvent
import gg.tropic.practice.minigame.event.MiniGameStartCancelEvent
import gg.tropic.practice.minigame.event.MiniGameStartTickEvent
import me.lucko.helper.Schedulers
import me.lucko.helper.scheduler.Task
import net.evilblock.cubed.util.CC
import org.bukkit.Bukkit
import org.bukkit.Sound
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
class MiniGamePreStartTask(
    private val game: AbstractMiniGameGameImpl<*>
) : Runnable, AutoCloseable
{
    companion object
    {
        fun AbstractMiniGameGameImpl<*>.startPreStartTask() = with(MiniGamePreStartTask(this)) {
            task = Schedulers.sync()
                .runRepeating(
                    this,
                    0L, TimeUnit.SECONDS,
                    1L, TimeUnit.SECONDS
                )

            game.with(this)
            task.bindWith(game)
        }
    }

    lateinit var task: Task

    override fun run()
    {
        val currentState = game.state
        if (!(currentState == GameState.Starting || currentState == GameState.Waiting))
        {
            task.closeAndReportException()
            return
        }

        val currentPlayers = game.expectationModel.players
        if (currentPlayers.isEmpty())
        {
            task.closeAndReportException()
            game.state = GameState.Completed
            game.closeAndCleanup()
            return
        }

        if (currentState == GameState.Waiting)
        {
            if (currentPlayers.size >= game.orchestrator.minimumPlayersRequiredToEnterStarting)
            {
                game.state = GameState.Starting
                MiniGameEnterStartEvent(game).callEvent()

                game.startCountDown = game.orchestrator.startGameCountDown
                run()
            }
            return
        }

        if (currentPlayers.size < game.orchestrator.minimumPlayersRequiredToEnterStarting)
        {
            game.playSound(Sound.NOTE_STICKS, 0.5f)
            game.sendMessage("${CC.RED}Waiting for more players as the game does not have enough to start...")
            game.state = GameState.Waiting

            MiniGameStartCancelEvent(game).callEvent()
            return
        }

        if (game.startCountDown == 0)
        {
            val event = GameStartEvent(game)
            Bukkit.getPluginManager().callEvent(event)

            if (event.isCancelled)
            {
                game.state = GameState.Completed
                game.closeAndCleanup()
                return
            }

            task.closeAndReportException()

            game.state = GameState.Playing
            game.startTimestamp = System.currentTimeMillis()
            return
        }

        MiniGameStartTickEvent(game, game.startCountDown).callEvent()
        game.startCountDown -= 1
    }

    override fun close()
    {

    }
}
