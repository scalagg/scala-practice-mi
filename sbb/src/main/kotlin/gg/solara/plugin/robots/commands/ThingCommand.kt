package gg.solara.plugin.robots.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.flavor.inject.Inject
import gg.tropic.practice.games.GameService
import gg.solara.plugin.robots.SolaraRobotsPlugin
import gg.solara.plugin.robots.bot.impl.SolaraPracticeRobotInstanceImpl

/**
 * @author GrowlyX
 * @since 3/8/2024
 */
@AutoRegister
object ThingCommand : ScalaCommand()
{
    @Inject
    lateinit var plugin: SolaraRobotsPlugin

    @CommandAlias("testingthing")
    @CommandPermission("op")
    fun onReplicationHealth(sender: ScalaPlayer)
    {
        GameService.byPlayer(sender.bukkit())?.robotInstance?.firstOrNull()
            ?.apply {
                plugin.featureManager.pearling.testPearl(
                    (this as SolaraPracticeRobotInstanceImpl).spawnedBot!!
                )
            }
    }
}
