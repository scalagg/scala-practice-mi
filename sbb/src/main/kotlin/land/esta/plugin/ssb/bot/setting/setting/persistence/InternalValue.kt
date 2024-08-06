package land.esta.plugin.ssb.bot.setting.setting.persistence

import land.esta.plugin.ssb.utils.RandomUtil
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
sealed interface InternalValue<T : Any> : AbstractTypeSerializable
{
    var internalValue: T
    fun randomize(minMax: Pair<Double, Double>): T
}

data class BooleanValue(
    override var internalValue: Boolean = false
) : InternalValue<Boolean>
{
    override fun randomize(minMax: Pair<Double, Double>) = internalValue
    override fun getAbstractType() = BooleanValue::class.java
}

data class DoubleValue(
    override var internalValue: Double = 0.0
) : InternalValue<Double>
{
    override fun randomize(minMax: Pair<Double, Double>): Double
    {
        val normalizedRange = max(minMax.first, 0.0)..minMax.second
        return Random.nextDouble(normalizedRange.start, normalizedRange.endInclusive)
    }

    override fun getAbstractType() = DoubleValue::class.java
}

data class IntValue(
    override var internalValue: Int = 0
) : InternalValue<Int>
{
    override fun randomize(minMax: Pair<Double, Double>): Int
    {
        val normalizedRange = max(minMax.first, 0.0)..minMax.second
        return Random.nextInt(
            floor(normalizedRange.start).toInt(),
            ceil(normalizedRange.endInclusive).toInt()
        )
    }

    override fun getAbstractType() = IntValue::class.java
}

fun Any.toInternalValue() = when (true)
{
    ((this is Boolean) || (this.javaClass == Boolean::class.javaObjectType) || (this.javaClass == Boolean::class.javaPrimitiveType)) -> BooleanValue(this as Boolean)
    ((this is Double) || (this.javaClass == Double::class.javaObjectType) || (this.javaClass == Double::class.javaPrimitiveType)) -> DoubleValue(this as Double)
    ((this is Int) || (this.javaClass == Int::class.javaObjectType) || (this.javaClass == Int::class.javaPrimitiveType)) -> IntValue(this as Int)
    else -> throw IllegalStateException(
        "Unsupported InternalValue type ${javaClass.name} was given"
    )
}
