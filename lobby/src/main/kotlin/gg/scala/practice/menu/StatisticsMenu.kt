package gg.scala.practice.menu

import gg.scala.lemon.util.QuickAccess.username
import gg.scala.practice.kit.Kit
import gg.scala.practice.kit.feature.FeatureFlag
import gg.scala.practice.profile.PracticeProfile
import gg.scala.practice.queue.QueueType
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

/**
 * @author Elb1to
 * @since 10/19/2023
 */
class StatisticsMenu(
    player: Player,
    private val profile: PracticeProfile,
    private var state: StatisticMenuState
) : TemplateKitMenu(player)
{
    override fun filterDisplayOfKit(player: Player, kit: Kit) =
        state == StatisticMenuState.Unranked || kit.features(FeatureFlag.Ranked) || (state == StatisticMenuState.Robot && kit.queueSizes.any { it.second.contains(
            QueueType.Robot) })
    override fun shouldIncludeKitDescription() = false

    override fun itemTitleFor(player: Player, kit: Kit) = when (state)
    {
        StatisticMenuState.Unranked ->
        {
            "${CC.B_YELLOW}${kit.displayName}"
        }
        StatisticMenuState.Robot ->
        {
            "${CC.B_GREEN}${kit.displayName}"
        }
        else ->
        {
            "${CC.B_PRI}${kit.displayName}"
        }
    }

    override fun itemDescriptionOf(player: Player, kit: Kit) =
        with(this) {
            val unrankedStats = profile.getUnrankedStatsFor(kit)
            val rankedStats = profile.getRankedStatsFor(kit)
            val unrankedBotStats = profile.getUnrankedBotStatsFor(kit)

            val unrankedLore = listOf(
                "${CC.WHITE}Wins: ${CC.YELLOW}${unrankedStats.wins}",
                "${CC.WHITE}Losses: ${CC.YELLOW}${unrankedStats.plays - unrankedStats.wins}",
                "${CC.WHITE}Played: ${CC.YELLOW}${unrankedStats.plays}",
                "",
                "${CC.WHITE}Kills: ${CC.YELLOW}${unrankedStats.kills}",
                "${CC.WHITE}Deaths: ${CC.YELLOW}${unrankedStats.deaths}",
                "",
                "${CC.YELLOW}Streaks:",
                "${CC.WHITE}Daily: ${CC.YELLOW}${unrankedStats.dailyStreak()}",
                "${CC.WHITE}Current: ${CC.YELLOW}${unrankedStats.streak}",
                "${CC.WHITE}Peak: ${CC.YELLOW}${unrankedStats.longestStreak}"
            )

            val rankedLore = listOf(
                "${CC.WHITE}Wins: ${CC.GOLD}${rankedStats.wins}",
                "${CC.WHITE}Losses: ${CC.GOLD}${rankedStats.plays - rankedStats.wins}",
                "${CC.WHITE}Played: ${CC.GOLD}${rankedStats.plays}",
                "",
                "${CC.WHITE}Kills: ${CC.GOLD}${rankedStats.kills}",
                "${CC.WHITE}Deaths: ${CC.GOLD}${rankedStats.deaths}",
                "",
                "${CC.WHITE}ELO: ${CC.GOLD}${rankedStats.elo}",
                "${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.WHITE} Highest: ${CC.GOLD}${
                    rankedStats.highestElo
                }",
                "",
                "${CC.GOLD}Streaks:",
                "${CC.WHITE}Daily: ${CC.GOLD}${rankedStats.dailyStreak()}",
                "${CC.WHITE}Current: ${CC.GOLD}${rankedStats.streak}",
                "${CC.WHITE}Peak: ${CC.GOLD}${rankedStats.longestStreak}"
            )

            val robotLore = listOf(
                "${CC.WHITE}Wins: ${CC.GREEN}${unrankedBotStats.wins}",
                "${CC.WHITE}Losses: ${CC.GREEN}${unrankedBotStats.plays - unrankedBotStats.wins}",
                "${CC.WHITE}Played: ${CC.GREEN}${unrankedBotStats.plays}",
                "",
                "${CC.WHITE}Kills: ${CC.GREEN}${unrankedBotStats.kills}",
                "${CC.WHITE}Deaths: ${CC.GREEN}${unrankedBotStats.deaths}",
                "",
                "${CC.GREEN}Streaks:",
                "${CC.WHITE}Daily: ${CC.GREEN}${unrankedBotStats.dailyStreak()}",
                "${CC.WHITE}Current: ${CC.GREEN}${unrankedBotStats.streak}",
                "${CC.WHITE}Peak: ${CC.GREEN}${unrankedBotStats.longestStreak}"
            )

            when (state)
            {
                StatisticMenuState.Unranked -> unrankedLore
                StatisticMenuState.Ranked -> rankedLore
                StatisticMenuState.Robot -> robotLore
            }
        }

    override fun itemClicked(player: Player, kit: Kit, type: ClickType)
    {

    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()
        val globalStats = profile.globalStatistics

        buttons[40] = ItemBuilder(Material.ITEM_FRAME)
            .name("${CC.PRI}Leaderboards")
            .addToLore(
                CC.GRAY + "View the top players",
                CC.GRAY + "on the server.",
                "",
                "${CC.WHITE}Leaderboards show the",
                "${CC.WHITE}best players based on",
                "${CC.WHITE}various stats.",
                "",
                CC.GREEN + "Click to open!"
            )
            .toButton { _, _ ->
                LeaderboardsMenu(player).openMenu(player)
            }

        buttons[5] = ItemBuilder
            .of(Material.ENDER_PORTAL_FRAME)
            .glow()
            .name("${CC.PRI}Toggle View")
            .addToLore(
                "${CC.GRAY}Select one of the following",
                "${CC.GRAY}views to see other statistics!",
                "",
                "${CC.WHITE}Current view:",
            )
            .apply {
                for (type in StatisticMenuState.entries)
                {
                    if (state == type)
                    {
                        addToLore("${CC.GREEN}► ${type.displayName}")
                    } else
                    {
                        addToLore("${CC.GRAY}${type.displayName}")
                    }
                }
            }
            .addToLore(
                "",
                "${CC.GREEN}Click to scroll through!"
            )
            .toButton { _, type ->
                state = if (type!!.isRightClick)
                    state.previous() else
                    state.next()

                openMenu(player)
                player.playSound(player.location, Sound.NOTE_STICKS, 1.0f, 1.0f)
            }

        buttons[3] = ItemBuilder
            .of(Material.NETHER_STAR)
            .name("${CC.PRI}Global")
            .setLore(
                listOf(
                    "${CC.WHITE}Wins: ${CC.SEC}${globalStats.totalWins}",
                    "${CC.WHITE}Losses: ${CC.SEC}${globalStats.totalLosses}",
                    "${CC.WHITE}Played: ${CC.SEC}${globalStats.totalPlays}",
                    "",
                    "${CC.WHITE}Kills: ${CC.SEC}${globalStats.totalKills}",
                    "${CC.WHITE}Deaths: ${CC.SEC}${globalStats.totalDeaths}"
                )
            )
            .toButton()

//        if (Bukkit.getPluginManager().isPluginEnabled("ScalaPractice-SmartBots")) {
//            buttons[38] = ItemBuilder
//                .of(
//                    if (state == StatisticMenuState.Robot)
//                        Material.WOOL else Material.CARPET
//                )
//                .apply {
//                    if (state == StatisticMenuState.Robot)
//                        glow()
//                }
//                .name("${CC.B_YELLOW}Robot Statistics")
//                .data(4)
//                .addToLore(
//                    " ",
//                    "${CC.GREEN}Click to view!"
//                )
//                .toButton { _, _ ->
//                    if (state == StatisticMenuState.Robot)
//                        return@toButton
//
//                    Button.playNeutral(player)
//
//                    StatisticsMenu(
//                        player,
//                        profile,
//                        StatisticMenuState.Robot
//                    ).openMenu(player)
//                }
//        }
//
//        buttons[39] = ItemBuilder
//            .of(
//                if (state == StatisticMenuState.Unranked)
//                    Material.WOOL else Material.CARPET
//            )
//            .apply {
//                if (state == StatisticMenuState.Unranked)
//                    glow()
//            }
//            .name("${CC.B_GREEN}Unranked Statistics")
//            .data(5)
//            .addToLore(
//                " ",
//                "${CC.GREEN}Click to view!"
//            )
//            .toButton { _, _ ->
//                if (state == StatisticMenuState.Unranked)
//                    return@toButton
//
//                Button.playNeutral(player)
//
//                StatisticsMenu(
//                    player,
//                    profile,
//                    StatisticMenuState.Unranked
//                ).openMenu(player)
//            }
//
//        buttons[41] = ItemBuilder
//            .of(
//                if (state == StatisticMenuState.Ranked)
//                    Material.WOOL else Material.CARPET
//            )
//            .name("${CC.B_PRI}Ranked Statistics")
//            .data(3)
//            .apply {
//                if (state == StatisticMenuState.Ranked)
//                    glow()
//            }
//            .addToLore(
//                " ",
//                "${CC.AQUA}Click to view!"
//            )
//            .toButton { _, _ ->
//                if (state == StatisticMenuState.Ranked)
//                    return@toButton
//
//                Button.playNeutral(player)
//
//                StatisticsMenu(
//                    player,
//                    profile,
//                    StatisticMenuState.Ranked
//                ).openMenu(player)
//            }

        return buttons
    }

    override fun getPrePaginatedTitle(player: Player) =
        "${profile.identifier.username()}'s ${state.name} Statistics"

    enum class StatisticMenuState(
        val displayName: String
    )
    {
        Ranked("Ranked"),
        Unranked("Unranked"),
        Robot("Robot");

        fun previous(): StatisticMenuState
        {
            return StatisticMenuState.entries
                .getOrNull(ordinal - 1)
                ?: StatisticMenuState.entries.last()
        }

        fun next(): StatisticMenuState
        {
            return StatisticMenuState.entries
                .getOrNull(ordinal + 1)
                ?: StatisticMenuState.entries.first()
        }
    }
}
