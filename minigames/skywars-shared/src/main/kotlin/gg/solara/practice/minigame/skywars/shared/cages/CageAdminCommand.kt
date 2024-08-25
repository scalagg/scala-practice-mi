package gg.solara.practice.minigame.skywars.shared.cages

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Default
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
@AutoRegister
@CommandAlias("cageadmin")
@CommandPermission("practice.command.cageadmin")
object CageAdminCommand : ScalaCommand()
{
    @Default
    fun help(player: ScalaPlayer) = CageViewAllMenu().openMenu(player.bukkit())
}
