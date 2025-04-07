package gg.scala.practice.commands

import gg.scala.commons.acf.annotation.*
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.practice.commands.menu.admin.PracticeLobbyAdminMenu

@AutoRegister
object ManagePracticeCommand : ScalaCommand()
{

    @CommandAlias("managepractice")
    @CommandPermission("practice.manage")
    @Description("Request an application reboot.")
    fun onManagePractice(player: ScalaPlayer)
    {
        PracticeLobbyAdminMenu().openMenu(player.bukkit())
    }
}