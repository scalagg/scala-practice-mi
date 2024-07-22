package land.esta.plugin.ssb.bot.setting.setting.persistence

import com.mojang.authlib.properties.Property
import land.esta.plugin.ssb.bot.setting.setting.Option
import land.esta.plugin.ssb.bot.setting.setting.RootAccessor
import land.esta.plugin.ssb.bot.setting.setting.root
import land.esta.plugin.ssb.bots.Bot
import net.evilblock.cubed.util.CC

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
class SettingProfile(val id: String)
{
    val settings = mutableMapOf<String, SettingValue>()

    var botName: String = "Bot"
    var skinProperties: Property = Bot.DEFAULT

    var randomizerEnabled: Boolean? = null

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

    fun describe() = listOf(
        "",
        "${CC.B_PRI}Difficulty:",
        "${CC.PRI}CPS: ${CC.WHITE}${
            value(RootAccessor.attackSettings.cps)
        }",
        "${CC.PRI}Reach: ${CC.WHITE}${
            "%.2f".format(value(RootAccessor.attackSettings.reach).toFloat())
        }",
        "${CC.PRI}Ping: ${CC.WHITE}${
            value(RootAccessor.connectionSettings.ping)
        }ms",
        "",
        "${CC.BD_AQUA}Traits:",
        "${CC.D_AQUA}W-Tap: ${CC.WHITE}${
            if (value(RootAccessor.wTapSettings.enabled)) "${CC.D_GREEN}✔" else "${CC.D_RED}✘"
        }",
        "${CC.D_AQUA}Rod: ${CC.WHITE}${
            if (value(RootAccessor.rodSettings.enabled)) "${CC.D_GREEN}✔" else "${CC.D_RED}✘"
        }",
        "${CC.D_AQUA}Strafe: ${CC.WHITE}${
            if (value(RootAccessor.strafeSettings.enabled)) "${CC.D_GREEN}✔" else "${CC.D_RED}✘"
        }",
    )

    fun setting(like: Option<*>) = settings[like.composeId()]
        ?: SettingValue(like.default.toInternalValue())

    fun <T : Any> value(like: Option<T>) = (settings[like.composeId()]
        ?.getObject()
        ?: like.default) as T
}
