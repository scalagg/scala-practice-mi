package gg.tropic.practice.minigame

import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializer

/**
 * @author GrowlyX
 * @since 8/23/2024
 */
object MiniGameSerializers
{
    fun configure()
    {
        Serializers.create {
            registerTypeAdapter(
                MiniGameConfiguration::class.java,
                AbstractTypeSerializer<MiniGameConfiguration>()
            )
        }
    }
}
