package gg.scala.practice.menu.manage.settings

import com.cryptomorin.xseries.XMaterial
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.practice.communications.PracticePlayerCommsService
import gg.scala.practice.configuration.PracticeConfigurationService
import gg.scala.practice.menu.manage.PracticeManagementMenu
import gg.scala.practice.menu.manage.settings.rollout.RolloutMenu
import gg.scala.practice.suffixWhenDev
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.entity.Player

/**
 * @author Topu
 * @date 3/7/2025
 */
class PracticeSettingsMenu : Menu("Practice Settings") {

    init {
        updateAfterClick = true
        placeholder = true
    }

    override fun size(buttons: Map<Int, Button>) = 27

    override fun getButtons(player: Player) = mapOf(
        10 to ItemBuilder
            .of(XMaterial.DIAMOND_SWORD)
            .name("${CC.B_GREEN}Ranked Queue")
            .addToLore(
                "${CC.GRAY}Enable or disable ranked queues,",
                "${CC.GRAY}including maps and matchmaking.",
                "",
                "${CC.WHITE}Current: ${CC.YELLOW}${PracticeConfigurationService.cached().rankedQueueEnabled}",
                "",
                "${CC.YELLOW}Click to toggle"
            )
            .toButton { _, _ ->
                Button.playNeutral(player)
                with(PracticeConfigurationService.cached()) {
                    rankedQueueEnabled = !rankedQueueEnabled
                    PracticeConfigurationService.sync(this)
                }
            },

        12 to ItemBuilder
            .of(XMaterial.NETHER_STAR)
            .name("${CC.B_GREEN}Ranked Queue Requirements")
            .addToLore(
                "${CC.GRAY}Set minimum requirements of wins",
                "${CC.GRAY}to queue ranked.",
                "",
                "${CC.WHITE}Current: ${CC.YELLOW}${PracticeConfigurationService.cached().rankedMinimumWinRequirement}",
                "",
                "${CC.GREEN}Left-Click to increase!",
                "${CC.YELLOW}Right-Click to decrease!"
            )
            .toButton { _, type ->
                if (type!!.isRightClick)
                {

                    Button.playSuccess(player)
                    with(PracticeConfigurationService.cached()) {
                        rankedMinimumWinRequirement = rankedMinimumWinRequirement?.minus(1)
                        PracticeConfigurationService.sync(this)
                    }
                    openMenu(player)
                    return@toButton
                }
                Button.playSuccess(player)
                with(PracticeConfigurationService.cached()) {
                    rankedMinimumWinRequirement = rankedMinimumWinRequirement?.plus(1)
                    PracticeConfigurationService.sync(this)
                }
                openMenu(player)
            },

        14 to ItemBuilder
            .of(XMaterial.PAPER)
            .name("${CC.B_GREEN}Synced Tablist")
            .addToLore(
                "${CC.GRAY}Enable or disable tablist syncing,",
                "${CC.GRAY}showing real-time player statuses.",
                "",
                "${CC.WHITE}Current: ${CC.YELLOW}${PracticeConfigurationService.cached().enableMIPTabHandler}",
                "",
                "${CC.YELLOW}Click to toggle"
            )
            .toButton { _, _ ->
                Button.playNeutral(player)
                with(PracticeConfigurationService.cached()) {
                    enableMIPTabHandler = !enableMIPTabHandler()
                    PracticeConfigurationService.sync(this)
                }
            },

        16 to ItemBuilder
            .of(XMaterial.REPEATER)
            .name("${CC.B_GREEN}Request Application Reboot")
            .addToLore(
                "${CC.GRAY}Restart the practice system to",
                "${CC.GRAY}apply recent configuration changes.",
                "",
                "${CC.GREEN}Click to reboot"
            )
            .toButton { _, _ ->
                Button.playNeutral(player)
                ConfirmMenu(
                    title = "Are you sure?",
                    confirm = true,
                    callback = { confirmed ->
                        if (!confirmed)
                        {
                            Tasks.sync { openMenu(player) }
                            return@ConfirmMenu
                        }

                        PracticePlayerCommsService.createMessage("reboot-app")
                            .publish(
                                AwareThreadContext.ASYNC,
                                channel = "practice:application".suffixWhenDev()
                            )
                    }
                ).openMenu(player)
            },

        11 to ItemBuilder
            .of(XMaterial.ENDER_EYE)
            .name("${CC.B_GREEN}Rollout")
            .addToLore(
                "${CC.GRAY}Manage rollout settings for",
                "${CC.GRAY}gradual deployment of updates.",
                "",
                "${CC.YELLOW}Click to open"
            )
            .toButton { _, _ ->
                Button.playNeutral(player)
                RolloutMenu().openMenu(player)
            },

        13 to ItemBuilder
            .of(XMaterial.WRITABLE_BOOK)
            .name("${CC.B_GREEN}Edit Login MOTD")
            .addToLore(
                "${CC.GRAY}Customize the message displayed",
                "${CC.GRAY}when players join the practice server.",
                "",
                "${CC.YELLOW}Click to edit"
            )
            .toButton { _, _ ->
                Button.playNeutral(player)
                with(PracticeConfigurationService.cached()) {
                    with(
                        object : TextEditorMenu(loginMOTD)
                        {
                            override fun getPrePaginatedTitle(player: Player) =
                                "Editing: Login MOTD"

                            override fun onClose(player: Player)
                            {

                            }

                            override fun onSave(player: Player, list: List<String>)
                            {
                                loginMOTD.clear()
                                loginMOTD.addAll(list)

                                PracticeConfigurationService.sync(this@with)
                                player.sendMessage("${CC.GREEN}Saved login MOTD text!")
                            }
                        }
                    ) {
                        openMenu(player)
                    }
                }
            }
    )

    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (manualClose)
        {
            Tasks.delayed(1L) {
                PracticeManagementMenu().openMenu(player)
            }
        }
    }
}
