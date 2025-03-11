package gg.scala.practice.utilities

import net.evilblock.cubed.util.CC

/**
 * @author Topu
 * @date 1/18/2025
 */
object BotMessages
{
    private val PREFIX = "${CC.GRAY}(From <bot>${CC.GRAY}) "

    enum class MessageType
    { WIN, LOSE }

    fun getBotWin(botName: String): String = formatMessage(MessageType.WIN, botName)

    fun getBotLose(botName: String): String = formatMessage(MessageType.LOSE, botName)

    private fun formatMessage(type: MessageType, botName: String): String
    {
        val randomMessage = messages[type]?.random() ?: "Whoops, I forgot what to say!"
        return PREFIX.replace("<bot>", botName) + randomMessage.replace("<bot>", botName)
    }

    private val messages = mapOf(
        MessageType.WIN to listOf(
            "Good game!",
            "Nice try!",
            "That was fun!",
            "Better luck next time!",
            "Keep practicing, you'll get better!",
            "You put up a good fight!",
            "That was a close one!",
            "You gave it your best shot!",
            "I had a great time playing with you!",
            "That was a great match!",
            "You're improving, keep it up!",
            "Well played!",
            "That was a tough battle!",
            "GG, let's play again sometime!",
            "You're a great competitor!",
            "Every game is a learning experience!",
            "That was a fun challenge!",
            "Hope to see you in the next match!"
        ),
        MessageType.LOSE to listOf(
            "Well played!",
            "Nice moves!",
            "That was a great match!",
            "You got me that time!",
            "You're really skilled!",
            "That was a tough one!",
            "I'll get you next time!",
            "You're getting better and better!",
            "GG, that was fun!",
            "I need to step up my game!",
            "You're a strong opponent!",
            "That was a fun challenge!",
            "I'm learning a lot from these matches!",
            "I almost had you!",
            "Great job, let's play again!",
            "Your skills are impressive!",
            "That was intense!",
            "You earned that win!",
            "GG, let's run it back!"
        )
    )
}