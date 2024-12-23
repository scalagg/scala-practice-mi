package gg.scala.practice.commands.admin

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.practice.kit.Kit
import gg.scala.practice.queue.QueueService
import gg.scala.practice.queue.QueueType

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
@AutoRegister
object RSWInternalCommand : ScalaCommand()
{
    @CommandAlias("rswinternal")
    @CommandPermission("op")
    fun rswJoinQueue(player: ScalaPlayer, kit: Kit)
    {
        QueueService.joinQueue(kit, QueueType.Ranked, 4, player.bukkit())
    }
}
