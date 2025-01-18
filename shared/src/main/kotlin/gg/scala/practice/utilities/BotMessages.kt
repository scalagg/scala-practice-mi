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
            "L",
            "You're literally so bad, you just got ratioed by Artificial Intelligence.",
            "E z",
            "You're so bad, why are you even playing?",
            "Imagine losing to a <bot>",
            "Ratio",
            "Haha noob",
            "Cope",
            "You can't be this bad can you?",
            "Might as well stop playing, you're so bad.",
            "You're so bad!",
            "I had my eyes closed, that's how easy you are",
            "You are easy work, I could probably beat you blindfolded according to my Artificial Intelligence.",
            "Honestly Dog Water, please uninstall the game!",
            "L Bozo",
            "You are definitely an F Tier player",
            "You suck",
            "You are a random",
            "You are the worst player I've ever fought according to my Artificial Intelligence."
        ),
        MessageType.LOSE to listOf(
            "Weird tryhard.",
            "Imagine trying on a <bot>, go to ranked weirdo.",
            "I literally just got on today.",
            "I hope you step on a puddle after putting on fresh socks.",
            "Go polish your crocs",
            "If I toss a stick, will you go away?",
            "I was warming up, but okay.",
            "LOL, Cheat Harder, I'm still better.",
            "I wasn't even trying so whatever.",
            "Go touch grass please",
            "I was lagging",
            "I hope you step on a Lego",
            "I hope both sides of your pillow are warm",
            "You're honestly so weird, imagine trying in a block game, grow up.",
            "You get no b*tches",
            "I hope you get banned.",
            "You're trash.",
            "Touch grass weirdo",
            "Your mother should have swallowed you",
            "No wonder your dad ran away",
            "Is your dad ever gonna come back with the milk?",
            "At least my dad came back"
        )
    )
}