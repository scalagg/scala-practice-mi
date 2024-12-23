package gg.scala.practice.minigame.skywars.chests.utilities

/**
 * @author GrowlyX
 * @since 8/26/2024
 */
object RandomUtilities
{
    @JvmStatic
    fun <T> of(list: List<T>) = list.randomOrNull()

    @JvmStatic
    fun <T> ofSingle(vararg list: T) = list.randomOrNull()

    @JvmStatic
    fun <T> ofMulti(vararg list: List<T>) = list.flatMap { it }.randomOrNull()
}
