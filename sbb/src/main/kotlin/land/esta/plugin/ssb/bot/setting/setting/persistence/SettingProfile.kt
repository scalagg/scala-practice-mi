package land.esta.plugin.ssb.bot.setting.setting.persistence

import com.mojang.authlib.properties.Property
import land.esta.plugin.ssb.bot.setting.setting.Option
import land.esta.plugin.ssb.bot.setting.setting.root
import land.esta.plugin.ssb.bots.Bot

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
class SettingProfile(val id: String)
{
    val settings = mutableMapOf<String, SettingValue>()

    var botName: String = "Bot"
    var skinProperties: Property = Bot.DEFAULT

    init
    {
        settings.apply {
            if (isNotEmpty())
            {
                return@apply
            }

            for (option in root.recursiveOptions())
            {
                settings[option.composeId()] = option.composeSettingValue()
            }
        }
    }

    fun updateRandomizer(option: Option<*>, randomizer: Double)
    {
        val optionID = option.composeId()
        settings.computeIfAbsent(optionID) { option.composeSettingValue() }
        settings[optionID]?.randomizer = randomizer
    }

    fun updateRandomizerRange(option: Option<*>, newMinMax: Pair<Double, Double>)
    {
        val optionID = option.composeId()
        settings.computeIfAbsent(optionID) { option.composeSettingValue() }
        settings[optionID]?.randomizerMinMax = newMinMax
    }

    fun <T : Any> update(option: Option<T>, newValue: T)
    {
        val optionID = option.composeId()
        settings.computeIfAbsent(optionID) { option.composeSettingValue() }
        settings[optionID]?.setObject(newValue)
    }

    fun setting(like: Option<*>) = settings[like.composeId()]
        ?: SettingValue(like.default.toInternalValue())

    fun value(like: Option<*>) = settings[like.composeId()]
        ?.getObject()
        ?: like.default
}
