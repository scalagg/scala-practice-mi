package gg.scala.practice.command

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.practice.games.GameService
import gg.scala.practice.kit.feature.FeatureFlag
import net.evilblock.cubed.util.bungee.BungeeUtil

/**
 * @author Topu
 * @date 1/18/2025
 */
@AutoRegister
object ForfeitCommand : ScalaCommand()
{

    @CommandAlias("forfeit|f")
    fun onForfeit(player: ScalaPlayer)
    {
        val game = GameService.byPlayer(player.bukkit()) ?: return
        game.forfeit(player.bukkit())
    }

}