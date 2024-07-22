package land.esta.plugin.ssb.bot.setting.setting.persistence

import com.mojang.authlib.properties.Property
import land.esta.plugin.ssb.bot.setting.setting.Option
import land.esta.plugin.ssb.bot.setting.setting.SettingLike
import land.esta.plugin.ssb.bots.Bot

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
class SettingProfile(private val id: String)
{
    val botName: String = "Bot"
    var skinProperties: Property = Bot.DEFAULT

    val settings = mutableMapOf<String, SettingValue>()

    fun value(like: Option<*>) = settings[like.composeId()]
        ?.getObject()
        ?: like.default
}
