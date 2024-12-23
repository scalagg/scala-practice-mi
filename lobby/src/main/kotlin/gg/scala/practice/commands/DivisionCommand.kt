package gg.scala.practice.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.annotations.commands.HighPriority
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.practice.divisions.DivisionMenu

/**
 * @author TastyCake
 * @date 9/7/2024
 */
@AutoRegister
@HighPriority
object DivisionCommand : ScalaCommand()
{
    @CommandAlias("division|div|title|divisions|titles")
    fun onDivisions(player: ScalaPlayer) {
        DivisionMenu().openMenu(player.bukkit())
    }
}