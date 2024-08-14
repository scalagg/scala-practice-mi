package gg.tropic.practice.commands.admin

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandCompletion
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Description
import gg.scala.commons.acf.annotation.Optional
import gg.scala.commons.acf.annotation.Subcommand
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.lemon.player.wrapper.AsyncLemonPlayer
import gg.tropic.practice.commands.networkTransaction
import gg.tropic.practice.kit.Kit
import gg.tropic.practice.profile.PracticeProfile
import gg.tropic.practice.statistics.GlobalStatistics
import gg.tropic.practice.statistics.KitStatistics
import gg.tropic.practice.statistics.ranked.RankedKitStatistics
import net.evilblock.cubed.util.CC
import org.bukkit.command.CommandSender

/**
 * @author GrowlyX
 * @since 12/23/2023
 */
@AutoRegister
@CommandAlias("eloupdates")
@CommandPermission("practice.command.eloupdates")
object ELOUpdatesCommand : ScalaCommand()
{
    @Subcommand("add")
    @Description("Add a specific amount of ELO to a player's profile.")
    @CommandCompletion("@mip-players @kits")
    fun onAddELO(
        player: CommandSender,
        target: AsyncLemonPlayer,
        kit: Kit,
        amount: Int
    ) = target.validatePlayers(player, false) {
        it.uniqueId.networkTransaction {
            val statistics = getRankedStatsFor(kit)
            statistics.updateELO(statistics.elo + amount)
        }.thenRun {
            player.sendMessage("${CC.GREEN}Added ${CC.WHITE}$amount${CC.GREEN} ELO to ${CC.WHITE}${it.name}'s${CC.GREEN} profile")
        }
    }

    @Subcommand("remove")
    @Description("Remove a specific amount of ELO from a player's profile.")
    @CommandCompletion("@mip-players @kits")
    fun onRemoveELO(
        player: CommandSender,
        target: AsyncLemonPlayer,
        kit: Kit,
        amount: Int
    ) = target.validatePlayers(player, false) {
        it.uniqueId.networkTransaction {
            val statistics = getRankedStatsFor(kit)
            statistics.updateELO(statistics.elo - amount)
        }.thenRun {
            player.sendMessage("${CC.GREEN}Removed ${CC.WHITE}$amount${CC.GREEN} ELO from ${CC.WHITE}${it.name}'s${CC.GREEN} profile")
        }
    }
}
