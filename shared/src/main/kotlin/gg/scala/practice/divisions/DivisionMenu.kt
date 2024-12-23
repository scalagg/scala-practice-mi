package gg.scala.practice.divisions

import gg.scala.practice.profile.PracticeProfileService
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author TastyCake
 * @date 9/7/2024
 */
class DivisionMenu : PaginatedMenu()
{
    companion object {
        val slots = mutableListOf(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            19 + 9, 20 + 9, 21 + 9, 22 + 9, 23 + 9, 24 + 9, 25 + 9
        )
    }

    init
    {
        placeholdBorders = true
    }

    override fun getAllPagesButtonSlots(): List<Int> = slots

    override fun size(buttons: Map<Int, Button>): Int = 45
    override fun getMaxItemsPerPage(player: Player): Int = 25

    override fun getGlobalButtons(player: Player): Map<Int, Button>?
    {
        return mutableMapOf<Int, Button>().apply {
            this[4] = ItemBuilder
                .of(Material.REDSTONE_TORCH_ON)
                .name("${CC.GOLD}Info")
                .addToLore(
                    "${CC.GRAY}Divisions will be added",
                    "${CC.GRAY}to your chat prefix",
                    "${CC.GRAY}you can unlock divisions by winning",
                    "${CC.GRAY}against unranked bots!",
                    "",
                    "${CC.GRAY}Your wins: ${CC.YELLOW}${PracticeProfileService.find(player)?.globalStatistics!!.totalUnrankedBotWins}"
                )
                .toButton()
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> =
        mutableMapOf<Int, Button>().apply {
            val uniqueTitles = mutableSetOf<String>()
            var i = 0

            for (division in Divisions.entries) {
                if (division == Divisions.NONE) continue
                val baseName = division.id.split(" ")[0]
                val split = division.title.split(" ")
                var baseTitle = division.title.removeSuffix(split[split.size - 1])
                if (split.size == 1) {
                    baseTitle = division.title
                }
                if (!uniqueTitles.contains(baseName)) {
                    uniqueTitles.add(baseName)
                    var material = Material.BOOK

                    val hasMultipleLevels = Divisions.entries.any { it.id.startsWith(baseName) && it != division }

                    if (!hasMultipleLevels) {
                        material = Material.PAPER
                        baseTitle = division.title
                    }

                    this[i++] = ItemBuilder
                        .of(material)
                        .name(baseTitle)
                        .setLore(mutableListOf<String>().apply {
                            if (hasMultipleLevels) add("${CC.GREEN}Click to view levels")
                            else add("${CC.GRAY}Required wins: ${CC.GREEN}${division.requiredWins}")
                        })
                        .toButton { player, _ ->
                            Button.playNeutral(player!!)

                            if (hasMultipleLevels) {
                                DivisionMiniMenu(baseName).openMenu(player)
                            }
                        }
                }
            }
        }

    override fun getPrePaginatedTitle(player: Player): String = "Divisions"
}