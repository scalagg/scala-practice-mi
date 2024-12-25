package gg.scala.practice.menu

import gg.scala.lemon.util.QuickAccess.username
import gg.scala.practice.isSmartBotsEnabled
import gg.scala.practice.kit.Kit
import gg.scala.practice.kit.feature.FeatureFlag
import gg.scala.practice.profile.PracticeProfile
import gg.scala.practice.queue.QueueType
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

/**
 * @author Elb1to
 * @since 10/19/2023
 */
class StatisticsMenu(
    player: Player,
    private val profile: PracticeProfile,
    private val state: StatisticMenuState
) : TemplateKitMenu(player)
{
    override fun filterDisplayOfKit(player: Player, kit: Kit) =
        state == StatisticMenuState.Unranked || kit.features(FeatureFlag.Ranked) || (state == StatisticMenuState.UnrankedBot && kit.queueSizes.any { it.second.contains(
            QueueType.Robot) })
    override fun shouldIncludeKitDescription() = false

    override fun itemTitleFor(player: Player, kit: Kit) = when (state)
    {
        StatisticMenuState.Unranked ->
        {
            "${CC.B_GREEN}${kit.displayName}"
        }
        StatisticMenuState.UnrankedBot ->
        {
            "${CC.B_YELLOW}${kit.displayName}"
        }
        else ->
        {
            "${CC.B_AQUA}${kit.displayName}"
        }
    }

    override fun itemDescriptionOf(player: Player, kit: Kit) =
        with(this) {
            val unrankedStats = profile.getUnrankedStatsFor(kit)
            val rankedStats = profile.getRankedStatsFor(kit)
            val unrankedBotStats = profile.getUnrankedBotStatsFor(kit)

            val unrankedLore = listOf(
                "${CC.WHITE}Wins: ${CC.GREEN}${unrankedStats.wins}",
                "${CC.WHITE}Losses: ${CC.GREEN}${unrankedStats.plays - unrankedStats.wins}",
                "${CC.WHITE}Played: ${CC.GREEN}${unrankedStats.plays}",
                "",
                "${CC.WHITE}Kills: ${CC.GREEN}${unrankedStats.kills}",
                "${CC.WHITE}Deaths: ${CC.GREEN}${unrankedStats.deaths}",
                "",
                "${CC.GREEN}Streaks:",
                "${CC.WHITE}Daily: ${CC.GREEN}${unrankedStats.dailyStreak()}",
                "${CC.WHITE}Current: ${CC.GREEN}${unrankedStats.streak}",
                "${CC.WHITE}Peak: ${CC.GREEN}${unrankedStats.longestStreak}"
            )

            val rankedLore = listOf(
                "${CC.WHITE}Wins: ${CC.AQUA}${rankedStats.wins}",
                "${CC.WHITE}Losses: ${CC.AQUA}${rankedStats.plays - rankedStats.wins}",
                "${CC.WHITE}Played: ${CC.AQUA}${rankedStats.plays}",
                "",
                "${CC.WHITE}Kills: ${CC.AQUA}${rankedStats.kills}",
                "${CC.WHITE}Deaths: ${CC.AQUA}${rankedStats.deaths}",
                "",
                "${CC.WHITE}ELO: ${CC.AQUA}${rankedStats.elo}",
                "${CC.WHITE} ${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.WHITE} Highest: ${CC.AQUA}${
                    rankedStats.highestElo
                }",
                "",
                "${CC.AQUA}Streaks:",
                "${CC.WHITE}Daily: ${CC.AQUA}${rankedStats.dailyStreak()}",
                "${CC.WHITE}Current: ${CC.AQUA}${rankedStats.streak}",
                "${CC.WHITE}Peak: ${CC.AQUA}${rankedStats.longestStreak}"
            )

            val unrankedBotLore = listOf(
                "${CC.WHITE}Wins: ${CC.YELLOW}${unrankedBotStats.wins}",
                "${CC.WHITE}Losses: ${CC.YELLOW}${unrankedBotStats.plays - unrankedBotStats.wins}",
                "${CC.WHITE}Played: ${CC.YELLOW}${unrankedBotStats.plays}",
                "",
                "${CC.WHITE}Kills: ${CC.YELLOW}${unrankedBotStats.kills}",
                "${CC.WHITE}Deaths: ${CC.YELLOW}${unrankedBotStats.deaths}",
                "",
                "${CC.YELLOW}Streaks:",
                "${CC.WHITE}Daily: ${CC.YELLOW}${unrankedBotStats.dailyStreak()}",
                "${CC.WHITE}Current: ${CC.YELLOW}${unrankedBotStats.streak}",
                "${CC.WHITE}Peak: ${CC.YELLOW}${unrankedBotStats.longestStreak}"
            )

            when (state)
            {
                StatisticMenuState.Unranked -> unrankedLore
                StatisticMenuState.Ranked -> rankedLore
                StatisticMenuState.UnrankedBot -> unrankedBotLore
            }
        }

    override fun itemClicked(player: Player, kit: Kit, type: ClickType)
    {

    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()
        val globalStats = profile.globalStatistics

        buttons[40] = ItemBuilder
            .of(Material.NETHER_STAR)
            .name("${CC.D_AQUA}Global")
            .setLore(
                listOf(
                    "${CC.WHITE}Wins: ${CC.AQUA}${globalStats.totalWins}",
                    "${CC.WHITE}Losses: ${CC.AQUA}${globalStats.totalLosses}",
                    "${CC.WHITE}Played: ${CC.AQUA}${globalStats.totalPlays}",
                    "",
                    "${CC.WHITE}Kills: ${CC.AQUA}${globalStats.totalKills}",
                    "${CC.WHITE}Deaths: ${CC.AQUA}${globalStats.totalDeaths}"
                )
            )
            .toButton()

        if (isSmartBotsEnabled) {
            buttons[38] = ItemBuilder
                .of(
                    if (state == StatisticMenuState.UnrankedBot)
                        Material.WOOL else Material.CARPET
                )
                .apply {
                    if (state == StatisticMenuState.UnrankedBot)
                        glow()
                }
                .name("${CC.B_YELLOW}Unranked Bot Statistics")
                .data(4)
                .addToLore(
                    " ",
                    "${CC.GREEN}Click to view!"
                )
                .toButton { _, _ ->
                    if (state == StatisticMenuState.UnrankedBot)
                        return@toButton

                    Button.playNeutral(player)

                    StatisticsMenu(
                        player,
                        profile,
                        StatisticMenuState.UnrankedBot
                    ).openMenu(player)
                }
        }

        buttons[39] = ItemBuilder
            .of(
                if (state == StatisticMenuState.Unranked)
                    Material.WOOL else Material.CARPET
            )
            .apply {
                if (state == StatisticMenuState.Unranked)
                    glow()
            }
            .name("${CC.B_GREEN}Unranked Statistics")
            .data(5)
            .addToLore(
                " ",
                "${CC.GREEN}Click to view!"
            )
            .toButton { _, _ ->
                if (state == StatisticMenuState.Unranked)
                    return@toButton

                Button.playNeutral(player)

                StatisticsMenu(
                    player,
                    profile,
                    StatisticMenuState.Unranked
                ).openMenu(player)
            }

        buttons[41] = ItemBuilder
            .of(
                if (state == StatisticMenuState.Ranked)
                    Material.WOOL else Material.CARPET
            )
            .name("${CC.B_AQUA}Ranked Statistics")
            .data(3)
            .apply {
                if (state == StatisticMenuState.Ranked)
                    glow()
            }
            .addToLore(
                " ",
                "${CC.AQUA}Click to view!"
            )
            .toButton { _, _ ->
                if (state == StatisticMenuState.Ranked)
                    return@toButton

                Button.playNeutral(player)

                StatisticsMenu(
                    player,
                    profile,
                    StatisticMenuState.Ranked
                ).openMenu(player)
            }

        return buttons
    }

    override fun getPrePaginatedTitle(player: Player) =
        "${profile.identifier.username()}'s ${state.name} Statistics"

    enum class StatisticMenuState
    {
        Ranked, Unranked, UnrankedBot
    }
}
