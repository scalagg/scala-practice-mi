package gg.solara.minigame.skywars.chests

import com.comphenix.packetwrapper.WrapperPlayServerBlockAction
import com.comphenix.protocol.wrappers.BlockPosition
import org.bukkit.Location
import org.bukkit.Material

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
object ChestPacketCreator
{
    fun closeChest(location: Location) = sendChestAction(location, 0)
    fun openChest(location: Location) = sendChestAction(location, 1)

    fun sendChestAction(location: Location, action: Int)
    {
        val wrapper = WrapperPlayServerBlockAction()
        wrapper.blockType = Material.CHEST
        wrapper.location = BlockPosition(location.toVector())
        wrapper.byte1 = 1
        wrapper.byte2 = action
        wrapper.handle.setMeta("programmatically", true)

        location.world.players.forEach {
            wrapper.sendPacket(it)
        }
    }
}
