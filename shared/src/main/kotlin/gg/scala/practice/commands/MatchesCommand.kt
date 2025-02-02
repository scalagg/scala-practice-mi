package gg.scala.practice.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandCompletion
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.practice.commands.admin.matchlist.MatchListPlayerMenu
import gg.scala.practice.services.GameManagerService

/**
 * @author Topu
 * @date 2/2/2025
 */
@AutoRegister
object MatchesCommand : ScalaCommand()
{

    @CommandAlias("matches")
    @CommandCompletion("@mip-players")
    fun onMatches(player: ScalaPlayer) = GameManagerService
        .allGames()
        .thenAccept { references ->
            MatchListPlayerMenu(references).openMenu(player.bukkit())
        }
}