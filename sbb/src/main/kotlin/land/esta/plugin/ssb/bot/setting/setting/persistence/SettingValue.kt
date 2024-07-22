package land.esta.plugin.ssb.bot.setting.setting.persistence

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
class SettingValue
{
    private var value: InternalValue<*> = IntValue()

    private val updateBy = 1.0
    private var randomizer = 0.0

    private var min = 0.0
    private var max = 0.0

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
