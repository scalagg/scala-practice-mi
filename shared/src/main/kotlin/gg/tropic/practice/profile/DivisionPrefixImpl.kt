package gg.tropic.practice.profile

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.channel.channels.DefaultChatChannel
import gg.tropic.practice.divisions.Divisions
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

/**
 * @author TastyCake
 * @date 9/6/2024
 */
@Service
object DivisionPrefixImpl
{
    @Configure
    fun configure() {
        DefaultChatChannel.provideAdditionalPrefix { player ->
            LegacyComponentSerializer.legacySection()
                .deserialize(Divisions.valueOf(PracticeProfileService.find(player)!!.botDivision).title)
        }
    }
}