package gg.tropic.practice.command

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Subcommand
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.tropic.practice.games.features.FireballFeature

/**
 * @author GrowlyX
 * @since 7/31/2024
 */
@AutoRegister
@CommandAlias("fireball")
@CommandPermission("op")
object FireballDevCommand : ScalaCommand()
{
    @Subcommand("horizontal")
    fun onHorizontal(player: ScalaPlayer, horizontal: Double)
    {
        FireballFeature.fireballHorizontal = horizontal
        player.sendMessage("Updated")
    }

    @Subcommand("vertical")
    fun onVertical(player: ScalaPlayer, vertical: Double)
    {
        FireballFeature.fireballVertical = vertical
        player.sendMessage("Updated")
    }

    @Subcommand("size")
    fun onSize(player: ScalaPlayer, size: Double)
    {
        FireballFeature.fireballExplosionSize = size
        player.sendMessage("Updated")
    }
}
