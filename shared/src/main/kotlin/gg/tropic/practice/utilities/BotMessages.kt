package gg.tropic.practice.utilities

import org.bukkit.ChatColor
import kotlin.random.Random

/**
 * @author TastyCake
 * @date 8/18/2024
 */
object BotMessages
{
    val botWin = arrayListOf(
        "&7(from <bot>&7) L",
        "&7(from <bot>&7) You're literally so bad, you just got ratioed by Artificial Intelligence.",
        "&7(from <bot>&7) E z",
        "&7(from <bot>&7) You're so bad, why are you even playing?",
        "&7(from <bot>&7) Imagine losing to a <bot>",
        "&7(from <bot>&7) Ratio",
        "&7(from <bot>&7) Haha noob",
        "&7(from <bot>&7) Cope",
        "&7(from <bot>&7) You can't be this bad can you?",
        "&7(from <bot>&7) Might as well stop playing, you're so bad.",
        "&7(from <bot>&7) You're so bad!",
        "&7(from <bot>&7) I had my eyes closed, that's how easy you are",
        "&7(from <bot>&7) You are easy work, I could probably beat you blind folded according to my Artificial Intelligence.",
        "&7(from <bot>&7) Honestly Dog Water, please uninstall the game!",
        "&7(from <bot>&7) L Bozo",
        "&7(from <bot>&7) You are definitely an F Tier player",
        "&7(from <bot>&7) You suck",
        "&7(from <bot>&7) You are a random",
        "&7(from <bot>&7) You are the worst player I've ever fought according to my Artificial Intelligence."
    )

    val botLose = arrayListOf(
        "&7(from <bot>&7) Weird tryhard.",
        "&7(from <bot>&7) Imagine trying on a <bot>&7, go to ranked weirdo.",
        "&7(from <bot>&7) I literally just got on today.",
        "&7(from <bot>&7) I hope you step on a puddle after putting on fresh socks.",
        "&7(from <bot>&7) Go polish your crocs",
        "&7(from <bot>&7) If I toss a stick, will you go away?",
        "&7(from <bot>&7) I was warming but okay.",
        "&7(from <bot>&7) LOL, Cheat Harder, I'm still better.",
        "&7(from <bot>&7) I wasn't even trying so whatever.",
        "&7(from <bot>&7) Go touch grass please",
        "&7(from <bot>&7) I was lagging",
        "&7(from <bot>&7) I hope you step on a lego",
        "&7(from <bot>&7) I hope both sides of your pillow are warm",
        "&7(from <bot>&7) You're honestly so weird, imagine trying in a block game, grow up.",
        "&7(from <bot>&7) You get no b*tches",
        "&7(from <bot>&7) I hope you get banned.",
        "&7(from <bot>&7) You're trash.",
        "&7(from <bot>&7) Touch grass weirdo",
        "&7(from <bot>&7) Your mother should have swallowed you",
        "&7(from <bot>&7) No wonder your dad ran away",
        "&7(from <bot>&7) Is your dad ever gonna come back with the milk?",
        "&7(from <bot>&7) At least my dad came back"
    )

    fun getBotWin(botName: String) : String {
        val randomMessage = botWin[Random.nextInt(botWin.size)]

        return ChatColor.translateAlternateColorCodes('&', randomMessage.replace("<bot>", botName))
    }

    fun getBotLose(botName: String) : String {
        val randomMessage = botLose[Random.nextInt(botLose.size)]

        return ChatColor.translateAlternateColorCodes('&', randomMessage.replace("<bot>", botName))
    }
}