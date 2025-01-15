package gg.scala.practice.menu

import com.cryptomorin.xseries.XMaterial
import gg.scala.practice.configuration.PracticeConfigurationService
import gg.scala.practice.kit.administration.KitAdminMenu
import gg.scala.practice.kit.category.administration.KitCategoryAdminMenu
import gg.scala.practice.map.administration.MapAdminMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import org.bukkit.entity.Player

/**
 * @author Topu
 * @date 1/15/2025
 */
class PracticeManagementMenu : Menu("Practice Management")
{

    init
    {
        updateAfterClick = true
        placeholder = true
    }

    override fun size(buttons: Map<Int, Button>) = 54


    override fun getButtons(player: Player) = mapOf(
        13 to ItemBuilder
            .of(XMaterial.DIAMOND_SWORD)
            .name(
                "${CC.YELLOW}Kits"
            )
            .addToLore("${CC.GRAY}Click to manage...")
            .toButton { _, _ ->
                KitAdminMenu(player).openMenu(player)
                Button.playNeutral(player)
            },
        11 to ItemBuilder
            .of(XMaterial.MAP)
            .name(
                "${CC.YELLOW}Maps"
            )
            .addToLore("${CC.GRAY}Click to manage...")
            .toButton { _, _ ->
                MapAdminMenu().openMenu(player)
                Button.playNeutral(player)
            },

        15 to ItemBuilder
            .of(XMaterial.EMERALD)
            .name("${CC.YELLOW}Kit Categories")
            .addToLore("${CC.GRAY}Click to manage...")
            .toButton { _, _ ->
                KitCategoryAdminMenu().openMenu(player)
                Button.playNeutral(player)
            },
        28 to ItemBuilder
            .of(XMaterial.BOOK)
            .name("${CC.YELLOW}Ranked: ${CC.WHITE}${PracticeConfigurationService.cached().rankedQueueEnabled}")
            .addToLore("${CC.GRAY}Click to edit...")
            .toButton { _, _ ->
                Button.playSuccess(player)
                with(PracticeConfigurationService.cached()) {
                    PracticeConfigurationService.cached().rankedQueueEnabled = !PracticeConfigurationService.cached().rankedQueueEnabled
                    PracticeConfigurationService.sync(this)
                }
            },
        29 to ItemBuilder
            .of(XMaterial.BOOK)
            .name("${CC.YELLOW}Synced Tablist: ${CC.WHITE}${PracticeConfigurationService.cached().enableMIPTabHandler}")
            .addToLore("${CC.GRAY}Click to edit...")
            .toButton { _, _ ->
                Button.playSuccess(player)
                with(PracticeConfigurationService.cached()) {
                    PracticeConfigurationService.cached().enableMIPTabHandler = !PracticeConfigurationService.cached().enableMIPTabHandler!!
                    PracticeConfigurationService.sync(this)
                }
            },
        30 to ItemBuilder
            .of(XMaterial.BOOK)
            .name("${CC.YELLOW}Blocked Hits: ${CC.WHITE}${PracticeConfigurationService.cached().blockedHitCap}")
            .addToLore("${CC.GRAY}Click to edit...")
            .toButton { _, _ ->
                player.closeInventory()
                Button.playNeutral(player)

                InputPrompt()
                    .withText("Enter a the blocked hit cap")
                    .acceptInput { _, cap ->
                        PracticeConfigurationService.cached().blockedHitCap = cap.toInt()
                        PracticeConfigurationService.sync(PracticeConfigurationService.cached())

                        Button.playSuccess(player)
                        player.sendMessage("${CC.GREEN}Updated Blocked Hits cap to $cap!")
                        PracticeManagementMenu().openMenu(player)
                    }
                    .start(player)
            },
        31 to ItemBuilder
            .of(XMaterial.BOOK)
            .name("${CC.YELLOW}Minimum Wins for Ranked: ${CC.WHITE}${PracticeConfigurationService.cached().rankedMinimumWinRequirement}")
            .addToLore("${CC.GRAY}Click to edit...")
            .toButton { _, _ ->
                player.closeInventory()
                Button.playNeutral(player)

                InputPrompt()
                    .withText("Enter a new minimum wins requirement")
                    .acceptInput { _, twitter ->
                        PracticeConfigurationService.cached().rankedMinimumWinRequirement = twitter.toInt()
                        PracticeConfigurationService.sync(PracticeConfigurationService.cached())

                        Button.playSuccess(player)
                        player.sendMessage("${CC.GREEN}Updated the minimum wins requirement to $twitter!")
                        PracticeManagementMenu().openMenu(player)
                    }
                    .start(player)
            },
    )
}