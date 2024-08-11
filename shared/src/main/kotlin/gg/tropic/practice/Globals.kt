package gg.tropic.practice

import java.util.UUID

/**
 * @author GrowlyX
 * @since 7/16/2024
 */
object Globals
{
    @JvmStatic
    val POSSIBLE_PLAYER_BOT_UNIQUE_IDS = (1..9)
        .map {
            UUID.fromString("00000000-0000-0000-0000-00000000000$it")
        }
        .toTypedArray()
}
