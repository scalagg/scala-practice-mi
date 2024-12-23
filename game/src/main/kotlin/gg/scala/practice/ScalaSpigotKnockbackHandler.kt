package gg.scala.practice

import gg.scala.spigot.preset.KnockbackHandler
import net.evilblock.cubed.util.nms.MinecraftReflection
import net.minecraft.server.v1_8_R3.EntityPlayer
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 11/20/2021
 */
object ScalaSpigotKnockbackHandler
{
    fun handleKnockback(player: Player, id: String)
    {
        val entityPlayer = MinecraftReflection.getHandle(player) as EntityPlayer
        entityPlayer.knockbackPreset = KnockbackHandler.findPreset(id) ?: KnockbackHandler.activePreset
    }

    fun reset(player: Player)
    {
        val entityPlayer = MinecraftReflection.getHandle(player) as EntityPlayer
        entityPlayer.knockbackPreset = KnockbackHandler.activePreset
    }
}