package gg.tropic.practice.games.tasks.multiround

import gg.tropic.practice.games.GameImpl
import gg.tropic.practice.kit.feature.FeatureFlag
import me.lucko.helper.scheduler.Task
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 7/15/2024
 */
class MultiRoundPlayerRespawnTask(
    private val game: GameImpl
) : Runnable
{
    private var task by Delegates.notNull<Task>()
    private var tick = game
        .flagMetaData(FeatureFlag.TimeUserSpectatesAfterDeath, "value")
        ?.toIntOrNull() ?: 3

    override fun run()
    {

    }
}
