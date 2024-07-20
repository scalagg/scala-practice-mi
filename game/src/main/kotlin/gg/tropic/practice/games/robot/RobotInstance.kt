package gg.tropic.practice.games.robot

import me.lucko.helper.terminable.composite.CompositeTerminable

/**
 * @author GrowlyX
 * @since 7/20/2024
 */
abstract class RobotInstance
{
    val terminable = CompositeTerminable.create()
    abstract fun name(): String
}
