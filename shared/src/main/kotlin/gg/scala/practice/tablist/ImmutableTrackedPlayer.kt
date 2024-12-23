package gg.scala.practice.tablist

import gg.scala.lemon.player.nametag.DefaultNametagProvider
import gg.scala.lemon.player.sorter.SortedRankCache
import gg.scala.lemon.util.QuickAccess
import net.evilblock.cubed.util.nms.MinecraftReflection
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.abs

/**
 * @author GrowlyX
 * @since 8/18/2024
 *
 * Yeeted from TehNeon lmaooo
 */
class ImmutableTrackedPlayer(
    val uuid: UUID,
    val username: String,
    val latency: Int,
    val skin: String? = null,
    val skinSignature: String? = null,
    val teamName: String,
    val displayName: String,
)
{
    fun requiresUpdate(other: ImmutableTrackedPlayer): Boolean
    {
        return (abs(other.latency - latency) > 10) ||
            other.displayName != displayName ||
            other.teamName != teamName
    }

    companion object
    {
        fun of(player: Player): ImmutableTrackedPlayer
        {
            val profile = (player as CraftPlayer).handle.profile
            val skinProperty = profile.properties.values().firstOrNull { it.name == "textures" }
            val currentRank = QuickAccess.realRank(player)

            val nametag = DefaultNametagProvider.fetchNametag(player, player)

            return ImmutableTrackedPlayer(
                uuid = player.uniqueId,
                username = player.name,
                latency = MinecraftReflection.getPing(player),
                skin = skinProperty?.value,
                skinSignature = skinProperty?.signature,
                teamName = SortedRankCache.teamMappings[currentRank.uuid] ?: "Z",
                displayName = "${nametag?.prefix ?: ""}${player.name}${nametag?.suffix ?: ""}"
            )
        }
    }
}
