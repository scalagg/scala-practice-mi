package gg.solara.plugin.robots.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.Single
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.solara.plugin.robots.bot.menu.UnrankedAdminMenu

/**
 * @author TastyCake
 * @date 8/29/2024
 */
@AutoRegister
object UnrankedAdminCommand : ScalaCommand()
{
    @CommandAlias("unrankedadmin|ura")
    fun onUnrankedAdmin(player: ScalaPlayer, @Single arg: String) {
        if (player.bukkit().isOp) {
            if (arg.equals("fight", ignoreCase = true)) {

            }
            else UnrankedAdminMenu().openMenu(player.bukkit())
        }
    }
}