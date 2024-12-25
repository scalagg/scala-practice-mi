package gg.scala.practice.menu

import gg.scala.practice.kit.Kit
import gg.scala.practice.kit.feature.FeatureFlag
import gg.scala.practice.leaderboards.Reference
import gg.scala.practice.leaderboards.ReferenceLeaderboardType
import gg.scala.practice.menu.TemplateKitMenu
import gg.scala.practice.services.LeaderboardManagerService
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.math.Numbers
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

/**
 * @author GrowlyX
 * @since 12/16/2023
 */
class LeaderboardsMenu(player: Player) : TemplateKitMenu(player)
{
    private var menuState = ReferenceLeaderboardType.ELO
    override fun filterDisplayOfKit(player: Player, kit: Kit) =
        !menuState.enforceRanked || kit.features(FeatureFlag.Ranked)

    init
    {
        async = true
        updateAfterClick = true
    }

    private val scoreCache = mutableMapOf<Reference, Pair<Long?, Long?>>()

    override fun asyncLoadResources(player: Player, callback: (Boolean) -> Unit)
    {
        callback(true)
    }

    override fun itemTitleFor(player: Player, kit: Kit) = "${CC.B_PRI}${kit.displayName}"
    override fun shouldIncludeKitDescription() = false

    private fun localDescriptionOf(player: Player, reference: Reference): List<String>
    {
        var cachedScore = scoreCache[reference]

        if (cachedScore == null)
        {
            scoreCache[reference] = LeaderboardManagerService
                .getUserRankWithScore(player.uniqueId, reference)
                .join()
            cachedScore = scoreCache[reference]!!
        }

        val personalScore = listOf(
            "${CC.PRI}Your score: ${CC.WHITE}${cachedScore.second?.run { Numbers.format(this) }  ?: "${CC.D_GRAY}Loading..."} ${
                CC.GRAY + (cachedScore.first?.run { "(#${Numbers.format(this + 1)})" } ?: "")
            }"
        )

        return personalScore + LeaderboardManagerService
            .getCachedFormattedLeaderboards(reference)
    }

    override fun itemDescriptionOf(player: Player, kit: Kit) = localDescriptionOf(
        player,
        Reference(leaderboardType = menuState, kitID = kit.id)
    )

    override fun getGlobalButtons(player: Player): Map<Int, Button>
    {
        val buttons = super.getGlobalButtons(player)
            ?.toMutableMap() ?: mutableMapOf()

        buttons[5] = ItemBuilder
            .of(Material.ENDER_PORTAL_FRAME)
            .glow()
            .name("${CC.PRI}Toggle View")
            .addToLore(
                "${CC.GRAY}Select one of the following",
                "${CC.GRAY}views to see other leaderboards!",
                "",
                "${CC.WHITE}Current view:",
            )
            .apply {
                for (type in ReferenceLeaderboardType.entries)
                {
                    if (menuState == type)
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
            .apply {
                for (type in ReferenceLeaderboardType.entries) {
                    if ((!Bukkit.getPluginManager().isPluginEnabled("ScalaPractice-SmartBots") && (type == ReferenceLeaderboardType.UnrankedBotWins || type == ReferenceLeaderboardType.UnrankedBotStreak))) {
                        continue
                    }

                    if (menuState == type) {
                        addToLore("${CC.GREEN}► ${type.displayName}")
                    } else {
                        addToLore("${CC.GRAY}${type.displayName}")
                    }
                }
            }
            .addToLore(
                "",
                "${CC.GREEN}Click to scroll through!"
            )
            .toButton { _, type ->
                val validEntries = ReferenceLeaderboardType.entries.filter {
                    !(it == ReferenceLeaderboardType.UnrankedBotWins || it == ReferenceLeaderboardType.UnrankedBotStreak) || Bukkit.getPluginManager().isPluginEnabled("ScalaPractice-SmartBots")
                }
                val currentIndex = validEntries.indexOf(menuState)
                menuState = if (type!!.isRightClick)
                    validEntries.getOrElse(currentIndex - 1) { validEntries.last() }
                else
                    validEntries.getOrElse(currentIndex + 1) { validEntries.first() }

                openMenu(player)
                player.playSound(player.location, Sound.NOTE_STICKS, 1.0f, 1.0f)
            }

        buttons[3] = ItemBuilder
            .of(Material.NETHER_STAR)
            .name("${CC.PRI}Global")
            .setLore(
                localDescriptionOf(player, Reference(menuState, null))
            )
            .toButton()

        return buttons
    }

    override fun itemClicked(player: Player, kit: Kit, type: ClickType) = Unit
    override fun getPrePaginatedTitle(player: Player) = menuState.displayName + " Leaderboards"
}
