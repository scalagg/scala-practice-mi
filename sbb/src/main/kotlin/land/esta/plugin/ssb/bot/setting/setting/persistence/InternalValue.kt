package land.esta.plugin.ssb.bot.setting.setting.persistence

import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
interface InternalValue<T : Any> : AbstractTypeSerializable
{
    var internalValue: T
}

data class BooleanValue(
    override var internalValue: Boolean = false
) : InternalValue<Boolean>
{
    override fun getAbstractType() = BooleanValue::class.java
}

data class DoubleValue(
    override var internalValue: Double = 0.0
) : InternalValue<Double>
{
    override fun getAbstractType() = DoubleValue::class.java
}

data class IntValue(
    override var internalValue: Int = 0
) : InternalValue<Int>
{
    override fun getAbstractType() = IntValue::class.java
}
