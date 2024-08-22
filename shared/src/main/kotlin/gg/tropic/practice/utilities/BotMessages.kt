package gg.tropic.practice.utilities

import net.evilblock.cubed.util.CC
import org.bukkit.ChatColor
import kotlin.random.Random

/**
 * @author TastyCake
 * @date 8/18/2024
 */
object BotMessages
{
    val botWin = listOf(
        "${CC.GRAY}(from <bot>${CC.GRAY}) L",
        "${CC.GRAY}(from <bot>${CC.GRAY}) You're literally so bad, you just got ratioed by Artificial Intelligence.",
        "${CC.GRAY}(from <bot>${CC.GRAY}) E z",
        "${CC.GRAY}(from <bot>${CC.GRAY}) You're so bad, why are you even playing?",
        "${CC.GRAY}(from <bot>${CC.GRAY}) Imagine losing to a <bot>",
        "${CC.GRAY}(from <bot>${CC.GRAY}) Ratio",
        "${CC.GRAY}(from <bot>${CC.GRAY}) Haha noob",
        "${CC.GRAY}(from <bot>${CC.GRAY}) Cope",
        "${CC.GRAY}(from <bot>${CC.GRAY}) You can't be this bad can you?",
        "${CC.GRAY}(from <bot>${CC.GRAY}) Might as well stop playing, you're so bad.",
        "${CC.GRAY}(from <bot>${CC.GRAY}) You're so bad!",
        "${CC.GRAY}(from <bot>${CC.GRAY}) I had my eyes closed, that's how easy you are",
        "${CC.GRAY}(from <bot>${CC.GRAY}) You are easy work, I could probably beat you blind folded according to my Artificial Intelligence.",
        "${CC.GRAY}(from <bot>${CC.GRAY}) Honestly Dog Water, please uninstall the game!",
        "${CC.GRAY}(from <bot>${CC.GRAY}) L Bozo",
        "${CC.GRAY}(from <bot>${CC.GRAY}) You are definitely an F Tier player",
        "${CC.GRAY}(from <bot>${CC.GRAY}) You suck",
        "${CC.GRAY}(from <bot>${CC.GRAY}) You are a random",
        "${CC.GRAY}(from <bot>${CC.GRAY}) You are the worst player I've ever fought according to my Artificial Intelligence."
    )

    val botLose = listOf(
        "${CC.GRAY}(from <bot>${CC.GRAY}) Weird tryhard.",
        "${CC.GRAY}(from <bot>${CC.GRAY}) Imagine trying on a <bot>${CC.GRAY}, go to ranked weirdo.",
        "${CC.GRAY}(from <bot>${CC.GRAY}) I literally just got on today.",
        "${CC.GRAY}(from <bot>${CC.GRAY}) I hope you step on a puddle after putting on fresh socks.",
        "${CC.GRAY}(from <bot>${CC.GRAY}) Go polish your crocs",
        "${CC.GRAY}(from <bot>${CC.GRAY}) If I toss a stick, will you go away?",
        "${CC.GRAY}(from <bot>${CC.GRAY}) I was warming but okay.",
        "${CC.GRAY}(from <bot>${CC.GRAY}) LOL, Cheat Harder, I'm still better.",
        "${CC.GRAY}(from <bot>${CC.GRAY}) I wasn't even trying so whatever.",
        "${CC.GRAY}(from <bot>${CC.GRAY}) Go touch grass please",
        "${CC.GRAY}(from <bot>${CC.GRAY}) I was lagging",
        "${CC.GRAY}(from <bot>${CC.GRAY}) I hope you step on a lego",
        "${CC.GRAY}(from <bot>${CC.GRAY}) I hope both sides of your pillow are warm",
        "${CC.GRAY}(from <bot>${CC.GRAY}) You're honestly so weird, imagine trying in a block game, grow up.",
        "${CC.GRAY}(from <bot>${CC.GRAY}) You get no b*tches",
        "${CC.GRAY}(from <bot>${CC.GRAY}) I hope you get banned.",
        "${CC.GRAY}(from <bot>${CC.GRAY}) You're trash.",
        "${CC.GRAY}(from <bot>${CC.GRAY}) Touch grass weirdo",
        "${CC.GRAY}(from <bot>${CC.GRAY}) Your mother should have swallowed you",
        "${CC.GRAY}(from <bot>${CC.GRAY}) No wonder your dad ran away",
        "${CC.GRAY}(from <bot>${CC.GRAY}) Is your dad ever gonna come back with the milk?",
        "${CC.GRAY}(from <bot>${CC.GRAY}) At least my dad came back"
    )

    fun getBotWin(botName: String) = botWin.random().replace("<bot>", botName)
    fun getBotLose(botName: String) = botLose.random().replace("<bot>", botName)
}
