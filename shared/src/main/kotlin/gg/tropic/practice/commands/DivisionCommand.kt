package gg.tropic.practice.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.Default
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.tropic.practice.divisions.DivisionMenu

/**
 * @author TastyCake
 * @date 9/7/2024
 */
@AutoRegister
@CommandAlias("division|div|title|divisions|titles")
object DivisionCommand : ScalaCommand()
{
    @Default
    fun onDivisions(player: ScalaPlayer) {
        DivisionMenu().openMenu(player.bukkit())
    }
}