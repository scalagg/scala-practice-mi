package gg.scala.practice.commands.admin

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandCompletion
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Optional
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.lemon.player.wrapper.AsyncLemonPlayer
import gg.scala.practice.commands.networkTransaction
import gg.scala.practice.kit.Kit
import gg.scala.practice.profile.PracticeProfile
import gg.scala.practice.statistics.GlobalStatistics
import gg.scala.practice.statistics.KitStatistics
import gg.scala.practice.statistics.UnrankedBotKitStatistics
import gg.scala.practice.statistics.ranked.RankedKitStatistics
import net.evilblock.cubed.util.CC
import org.bukkit.command.CommandSender

/**
 * @author GrowlyX
 * @since 12/23/2023
 */
@AutoRegister
object ResetStatsCommand : ScalaCommand()
{
    enum class StatReset(
        val resetFor: (PracticeProfile, Kit?) -> Unit
    )
    {
        ELO({ profile, kit ->
            profile.rankedStatistics.forEach { (kitID, rankedStats) ->
                if (kit == null || kit.id == kitID)
                {
                    rankedStats.updateELO(1000)
                }
            }
        }),
        UnrankedStats({ profile, kit ->
            profile.unrankedStatistics.toMap().forEach { (kitID, _) ->
                if (kit == null || kit.id == kitID)
                {
                    profile.unrankedStatistics[kitID] = KitStatistics()
                }
            }
        }),
        RankedStats({ profile, kit ->
            profile.rankedStatistics.toMap().forEach { (kitID, _) ->
                if (kit == null || kit.id == kitID)
                {
                    profile.rankedStatistics[kitID] = RankedKitStatistics()
                }
            }
        }),
        UnrankedBotStats({ profile, kit ->
            profile.botStatistics.toMap().forEach { (kitID, _) ->
                if (kit == null || kit.id == kitID)
                {
                    profile.botStatistics[kitID] = UnrankedBotKitStatistics()
                }
            }
        }),
        GlobalStats({ profile, _ ->
            profile.globalStatistics = GlobalStatistics()
        }),
        AllStats({ profile, kit ->
            RankedStats.resetFor(profile, kit)
            GlobalStats.resetFor(profile, kit)
            UnrankedStats.resetFor(profile, kit)
        })
    }

    @CommandAlias("resetstats")
    @CommandCompletion("@mip-players * @kits")
    @CommandPermission("practice.command.resetstats")
    fun onResetStats(
        player: CommandSender,
        target: AsyncLemonPlayer,
        resetOptions: StatReset,
        @Optional kit: Kit?
    ) = target.validatePlayers(player, false) {
        it.uniqueId.networkTransaction {
            resetOptions.resetFor(this, kit)

            player.sendMessage(
                "${CC.GREEN}You have reset the ${CC.BOLD}${
                    resetOptions.name
                }${CC.GREEN} for ${CC.WHITE}${it.name}${CC.GREEN}${
                    if (kit != null) " on kit ${CC.PRI}${kit.id}${CC.GREEN}" else ""
                }."
            )
        }
    }
}
