package gg.tropic.practice.games.robot

import me.lucko.helper.terminable.composite.CompositeTerminable
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 7/20/2024
 */
abstract class RobotInstance
{
    val terminable = CompositeTerminable.create()
    var hasInitialSpawned = false

    abstract fun name(): String
    abstract fun ping(): Int

    abstract fun initialSpawn()

    abstract fun participantConnected(player: Player)
    abstract fun spectatorConnected(player: Player)

    abstract fun destroy()
}
