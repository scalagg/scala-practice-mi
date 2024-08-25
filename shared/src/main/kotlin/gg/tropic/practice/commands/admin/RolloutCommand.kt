package gg.tropic.practice.commands.admin

import gg.scala.aware.thread.AwareThreadContext
import gg.scala.commons.acf.CommandHelp
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Default
import gg.scala.commons.acf.annotation.Description
import gg.scala.commons.acf.annotation.HelpCommand
import gg.scala.commons.acf.annotation.Optional
import gg.scala.commons.acf.annotation.Subcommand
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.tropic.practice.orchestrator.OrchestratorService
import gg.tropic.practice.region.Region
import gg.tropic.practice.suffixWhenDev
import net.evilblock.cubed.util.CC

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
@AutoRegister
@CommandAlias("rollout")
@CommandPermission("op")
object RolloutCommand : ScalaCommand()
{
    @Default
    @HelpCommand
    fun help(help: CommandHelp) = help.showHelp()

    @Subcommand("start")
    @Description("Start a network update rollout.")
    fun start(player: ScalaPlayer, region: Region, @Optional dev: Boolean?)
    {
        player.sendMessage("${CC.GREEN}We've requested an update rollout, please wait a moment.")

        OrchestratorService
            .createMessage(
                "start",
                "region" to region,
                "dev" to (dev ?: false),
                "player" to player.uniqueId
            )
            .publish(
                AwareThreadContext.ASYNC,
                channel = "practice:orchestrator".suffixWhenDev()
            )
    }

    @Subcommand("end")
    @Description("End an network update rollout.")
    fun end(player: ScalaPlayer, region: Region)
    {
        player.sendMessage("${CC.GREEN}We've requested a rollout cancel, please wait a moment.")

        OrchestratorService
            .createMessage(
                "end",
                "region" to region,
                "player" to player.uniqueId
            )
            .publish(
                AwareThreadContext.ASYNC,
                channel = "practice:orchestrator".suffixWhenDev()
            )
    }
}
