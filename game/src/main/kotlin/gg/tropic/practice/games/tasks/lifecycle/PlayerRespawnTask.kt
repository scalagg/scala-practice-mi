package gg.tropic.practice.games.tasks.lifecycle

import gg.tropic.practice.games.GameImpl
import gg.tropic.practice.kit.feature.FeatureFlag
import me.lucko.helper.Schedulers
import me.lucko.helper.scheduler.Task
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 7/15/2024
 */
class PlayerRespawnTask(
    private val player: Player,
    private val game: GameImpl
) : Runnable, AutoCloseable
{
    companion object
    {
        fun GameImpl.startDelayedRespawn(player: Player) = with(PlayerRespawnTask(player, this)) {
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

    private var task by Delegates.notNull<Task>()
    private var tick = game
        .flagMetaData(FeatureFlag.TimeUserSpectatesAfterDeath, "value")
        ?.toIntOrNull() ?: 3

    override fun run()
    {
        if (tick <= 0)
        {


            task.closeAndReportException()
            return
        }


    }

    override fun close()
    {

    }
}
