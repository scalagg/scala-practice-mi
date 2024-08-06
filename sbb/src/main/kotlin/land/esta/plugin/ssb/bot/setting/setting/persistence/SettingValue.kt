package land.esta.plugin.ssb.bot.setting.setting.persistence

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
data class SettingValue(
    private var value: InternalValue<*> = IntValue(),
    var randomizer: Double = 0.0
)
{
    var randomizerMinMax = 0.0 to 0.0

    fun getMin() = randomizerMinMax.first
    fun getMax() = randomizerMinMax.second

    fun getObject() = value.internalValue
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
