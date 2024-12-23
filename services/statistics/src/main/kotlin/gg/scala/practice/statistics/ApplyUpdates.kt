package gg.scala.practice.statistics

/**
 * @author GrowlyX
 * @since 9/21/2023
 */
class ApplyUpdates<T>(
    private val update: List<(T) -> Unit>
)
{
    fun apply(value: T)
    {
        update.forEach { it(value) }
    }
}
