package gg.tropic.practice.command

import com.lunarclient.apollo.Apollo
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.tropic.practice.autoscale.ReplicationAutoScaleTask
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.time.TimeUtil
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

/**
 * @author GrowlyX
 * @since 3/8/2024
 */
@AutoRegister
object ThingCommand : ScalaCommand()
{
    @CommandAlias("testingthing")
    @CommandPermission("op")
    fun onReplicationHealth(sender: CommandSender)
    {
        sender.sendMessage(
            Apollo.getWorldManager()
                .worlds.size.toString()
        )

        sender.sendMessage(
            Bukkit.getWorlds().size.toString()
        )
    }
}
