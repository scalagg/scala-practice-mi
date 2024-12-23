package gg.scala.practice.minigame.event

import gg.scala.commons.event.StatelessEvent
import gg.scala.practice.minigame.AbstractMiniGameGameImpl

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
data class MiniGameStartCancelEvent(
    val game: AbstractMiniGameGameImpl<*>
) : StatelessEvent()
