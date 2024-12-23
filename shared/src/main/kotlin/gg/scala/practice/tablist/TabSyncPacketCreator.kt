package gg.scala.practice.tablist

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedGameProfile
import com.comphenix.protocol.wrappers.WrappedSignedProperty
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.GameMode

object TabSyncPacketCreator {

    private fun constructPacket(action: Set<EnumWrappers.PlayerInfoAction>, updates: List<ImmutableTrackedPlayer>): List<WrapperPlayServerPlayerInfo> {
        val mappedUpdates = updates.mapToUpdates()
        return action.map {
            val packet = WrapperPlayServerPlayerInfo()
            packet.action = it
            packet.data = mappedUpdates
            packet
        }
    }

    fun create(updates: List<ImmutableTrackedPlayer>): List<WrapperPlayServerPlayerInfo> {
        return constructPacket(setOf(EnumWrappers.PlayerInfoAction.ADD_PLAYER), updates)
    }

    fun update(updates: List<ImmutableTrackedPlayer>): List<WrapperPlayServerPlayerInfo> {
        return constructPacket(setOf(EnumWrappers.PlayerInfoAction.UPDATE_LATENCY, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME), updates)
    }

    fun remove(updates: List<ImmutableTrackedPlayer>): List<WrapperPlayServerPlayerInfo> {
        return constructPacket(setOf(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), updates)
    }

    fun List<ImmutableTrackedPlayer>.mapToUpdates() = map { syncData ->
        val profile = WrappedGameProfile(syncData.uuid, syncData.username)
        if (syncData.skin != null && syncData.skinSignature != null)
            profile.properties.put("textures", WrappedSignedProperty("textures", syncData.skin, syncData.skinSignature))
        val gameMode = NativeGameMode.fromBukkit(GameMode.SURVIVAL)
        val component = WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(
            LegacyComponentSerializer.legacySection().deserialize(syncData.displayName)
        ))

        PlayerInfoData(profile.uuid, syncData.latency ?: 0, true, gameMode, profile, component)
    }

}
