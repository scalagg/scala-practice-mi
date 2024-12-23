package gg.scala.practice.queue

/**
 * @author GrowlyX
 * @since 9/21/2023
 */
enum class QueueType(
    val coinMultiplier: Double
)
{
    Unranked(1.0),
    Ranked(1.5),
    Robot(0.0)
}
