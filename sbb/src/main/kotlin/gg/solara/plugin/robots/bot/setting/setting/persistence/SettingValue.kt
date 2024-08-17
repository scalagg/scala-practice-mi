package gg.solara.plugin.robots.bot.setting.setting.persistence

import land.esta.plugin.ssb.bot.setting.setting.persistence.BooleanValue
import land.esta.plugin.ssb.bot.setting.setting.persistence.DoubleValue
import land.esta.plugin.ssb.bot.setting.setting.persistence.IntValue
import land.esta.plugin.ssb.bot.setting.setting.persistence.InternalValue

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
data class SettingValue(
    private var value: InternalValue<*> = IntValue(),
    var randomizer: Double = 0.0
)
{

    @Transient
    var shouldLocalRandomize: Boolean = false

    fun getObject() = if (shouldLocalRandomize)
        value.randomize(randomizer) else value.internalValue
    fun setObject(possible: Any)
    {
        value = if (possible is Boolean)
        {
            BooleanValue(possible)
        } else if (possible is Int)
        {
            IntValue(possible)
        } else
        {
            DoubleValue(possible as Double)
        }
    }
}
