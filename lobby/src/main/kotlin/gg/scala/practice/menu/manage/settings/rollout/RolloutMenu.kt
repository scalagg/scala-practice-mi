package gg.scala.practice.menu.manage.settings.rollout

import com.cryptomorin.xseries.XMaterial
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.practice.menu.manage.settings.PracticeSettingsMenu
import gg.scala.practice.orchestrator.OrchestratorService
import gg.scala.practice.suffixWhenDev
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.entity.Player

/**
 * @author Topu
 * @date 3/7/2025
 */
class RolloutMenu : Menu("Request a Rollout")
{
    init
    {
        updateAfterClick = true
        placeholder = true
    }

    override fun size(buttons: Map<Int, Button>) = 27


    override fun getButtons(player: Player) = mapOf(
        11 to ItemBuilder
            .of(XMaterial.BLUE_WOOL)
            .name(
                "${CC.B_GREEN}NA"
            )
            .addToLore(
                "${CC.GRAY}Rollout the NA region,",
                "${CC.GRAY}for updates.",
                "",
                "${CC.YELLOW}Click to rollout"
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

                        player.sendMessage("${CC.GREEN}We've requested an update rollout, please wait a moment.")
                        OrchestratorService
                            .createMessage(
                                "start",
                                "region" to "NA",
                                "dev" to false,
                                "player" to player.uniqueId
                            )
                            .publish(
                                AwareThreadContext.ASYNC,
                                channel = "practice:orchestrator".suffixWhenDev()
                            )
                    }
                ).openMenu(player)
            },

        13 to ItemBuilder
            .of(XMaterial.RED_WOOL)
            .name(
                "${CC.B_GREEN}Both"
            )
            .addToLore(
                "${CC.GRAY}Rollout both EU & NA regions,",
                "${CC.GRAY}for updates.",
                "",
                "${CC.YELLOW}Click to rollout"
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

                        player.sendMessage("${CC.GREEN}We've requested an update rollout, please wait a moment.")
                        OrchestratorService
                            .createMessage(
                                "start",
                                "region" to "Both",
                                "dev" to false,
                                "player" to player.uniqueId
                            )
                            .publish(
                                AwareThreadContext.ASYNC,
                                channel = "practice:orchestrator".suffixWhenDev()
                            )
                    }
                ).openMenu(player)
            },

        15 to ItemBuilder
            .of(XMaterial.LIGHT_BLUE_WOOL)
            .name("${CC.B_GREEN}EU")
            .addToLore(
                "${CC.GRAY}Rollout the EU region,",
                "${CC.GRAY}for updates.",
                "",
                "${CC.YELLOW}Click to rollout"
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

                        player.sendMessage("${CC.GREEN}We've requested an update rollout, please wait a moment.")
                        OrchestratorService
                            .createMessage(
                                "start",
                                "region" to "EU",
                                "dev" to false,
                                "player" to player.uniqueId
                            )
                            .publish(
                                AwareThreadContext.ASYNC,
                                channel = "practice:orchestrator".suffixWhenDev()
                            )
                    }
                ).openMenu(player)
            },
    )

    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (manualClose)
        {
            Tasks.delayed(1L) {
                PracticeSettingsMenu().openMenu(player)
            }
        }
    }
}