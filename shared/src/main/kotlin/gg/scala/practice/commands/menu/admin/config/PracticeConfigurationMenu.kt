package gg.scala.practice.commands.menu.admin.config

import com.cryptomorin.xseries.XMaterial
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.practice.communications.PracticePlayerCommsService
import gg.scala.practice.configuration.PracticeConfiguration
import gg.scala.practice.configuration.PracticeConfigurationService
import gg.scala.practice.map.metadata.anonymous.Position
import gg.scala.practice.suffixWhenDev
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import org.bukkit.entity.Player

/**
 * Class created on 1/17/2025

 * @author Max C.
 * @project esta-practice
 * @website https://solo.to/redis
 */
class PracticeConfigurationMenu : Menu()
{
    init
    {
        placeholder = true
        updateAfterClick = true
    }

    val config = PracticeConfigurationService.cached()

    override fun getButtons(player: Player): Map<Int, Button>
    {
        return mutableMapOf<Int, Button>().also {
            it[10] = ItemBuilder.of(XMaterial.EMERALD)
                .name("${CC.B_GREEN}Ranked Play")
                .addToLore(
                    "${CC.GRAY}Change whether or not ranked play",
                    "${CC.GRAY}is supported on this server.",
                    "${CC.GRAY}Currently: ${if (config.rankedQueueEnabled) "${CC.GREEN}Enabled" else "${CC.RED}Disabled"}",
                    "",
                    if (config.rankedQueueEnabled) "${CC.RED}Click to disable" else "${CC.GREEN}Click to enable"
                ).toButton { _, _ ->
                    config.rankedQueueEnabled = !config.rankedQueueEnabled
                    PracticeConfigurationService.sync(config)
                }

            it[11] = ItemBuilder.of(XMaterial.OAK_SIGN)
                .name("${CC.B_GREEN}MIP Tab Handler")
                .addToLore(
                    "${CC.GRAY}Change whether or not the",
                    "${CC.GRAY}MIP Tab Handler is enabled.",
                    "${CC.GRAY}Currently: ${if (config.enableMIPTabHandler()) "${CC.GREEN}Enabled" else "${CC.RED}Disabled"}",
                    "",
                    if (config.enableMIPTabHandler()) "${CC.RED}Click to disable" else "${CC.GREEN}Click to enable"
                ).toButton { _, _ ->
                    config.enableMIPTabHandler = !config.enableMIPTabHandler()
                    PracticeConfigurationService.sync(config)
                }

            it[12] = ItemBuilder.of(XMaterial.DIAMOND_HORSE_ARMOR)
                .name("${CC.B_GREEN}Ranked Win Requirement")
                .addToLore(
                    "${CC.GRAY}Change the amount of wins that",
                    "${CC.GRAY}are needed in order to play the",
                    "${CC.GRAY}ranked gamemode.",
                    "${CC.GRAY}Currently: ${CC.WHITE}${config.minimumWinRequirement()}",
                    "",
                    "${CC.GREEN}Left-Click to increase!",
                    "${CC.YELLOW}Shift-Click to set!",
                    "${CC.RED}Right-Click to decrease!"
                ).toButton { _, type ->
                    if (type!!.isRightClick)
                    {

                        Button.playSuccess(player)
                        config.rankedMinimumWinRequirement = config.rankedMinimumWinRequirement?.minus(1)
                        PracticeConfigurationService.sync(config)
                        openMenu(player)
                        return@toButton
                    }
                    if (type.isShiftClick)
                    {
                        Button.playSuccess(player)
                        InputPrompt()
                            .withText("${CC.GREEN}Enter the amount of wins that are needed int order to play the ranked gamemode:")
                            .acceptInput { _, input ->
                                var amount: Int? = null

                                try
                                {
                                    amount = Integer.parseInt(input)
                                } catch (e: Exception)
                                {
                                    player.sendMessage("${CC.RED}Invalid integer.")
                                }

                                config.rankedMinimumWinRequirement = amount
                                PracticeConfigurationService.sync(config)
                                PracticeConfigurationMenu().openMenu(player)
                            }.start(player)
                        return@toButton
                    }
                    Button.playSuccess(player)
                    with(PracticeConfigurationService.cached()) {
                        rankedMinimumWinRequirement = rankedMinimumWinRequirement?.plus(1)
                        PracticeConfigurationService.sync(this)
                    }
                    openMenu(player)
                }

            it[13] = ItemBuilder.of(XMaterial.IRON_HELMET)
                .name("${CC.B_GREEN}Blocked Hit Cap")
                .addToLore(
                    "${CC.GRAY}Change the active blocked",
                    "${CC.GRAY}hit cap.",
                    "${CC.GRAY}Currently: ${CC.WHITE}${config.blockedHitCap}",
                    "",
                    "${CC.GREEN}Left-Click to increase!",
                    "${CC.YELLOW}Shift-Click to set!",
                    "${CC.RED}Right-Click to decrease!"
                ).toButton { _, type ->
                    if (type!!.isRightClick)
                    {

                        Button.playSuccess(player)
                        config.blockedHitCap = config.blockedHitCap?.minus(1)
                        PracticeConfigurationService.sync(config)
                        openMenu(player)
                        return@toButton
                    }
                    if (type.isShiftClick)
                    {
                        Button.playSuccess(player)
                        InputPrompt()
                            .withText("${CC.GREEN}Enter the new blocked hit cap that you want the server to have:")
                            .acceptInput { _, input ->
                                var amount: Int? = null

                                try
                                {
                                    amount = Integer.parseInt(input)
                                } catch (e: Exception)
                                {
                                    player.sendMessage("${CC.RED}Invalid integer.")
                                }

                                config.blockedHitCap = amount
                                PracticeConfigurationService.sync(config)
                                PracticeConfigurationMenu().openMenu(player)
                            }.start(player)
                        return@toButton
                    }
                    Button.playSuccess(player)
                    config.blockedHitCap = config.blockedHitCap?.plus(1)
                    PracticeConfigurationService.sync(config)
                    openMenu(player)
                }

            it[14] = ItemBuilder.of(XMaterial.COMPASS)
                .name("${CC.B_GREEN}Change Spawn Point")
                .addToLore(
                    "${CC.GRAY}Change spawn point that is being",
                    "${CC.GRAY}used on this server.",
                    "${CC.GRAY}Currently: ${CC.WHITE}${config.spawnLocation.x.toInt()}, ${config.spawnLocation.y.toInt()}, ${config.spawnLocation.z.toInt()}",
                    "",
                    "${CC.GREEN}Click to change!"
                ).toButton { _, _ ->
                    config.spawnLocation =
                        Position(player.x, player.y, player.z, player.location.yaw, player.location.pitch)
                    PracticeConfigurationService.sync(config)
                    PracticeConfigurationMenu().openMenu(player)
                }

            it[15] = ItemBuilder.of(XMaterial.WRITABLE_BOOK)
                .name("${CC.B_GREEN}Edit Login MOTD")
                .addToLore(
                    "${CC.GRAY}Customize the message displayed",
                    "${CC.GRAY}when players join the practice lobby.",
                    "",
                    "${CC.GREEN}Click to configure"
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

            it[16] = ItemBuilder.of(XMaterial.REPEATER)
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
                }
        }
    }

    override fun getTitle(player: Player): String
    {
        return "Select a Setting"
    }

    override fun size(buttons: Map<Int, Button>): Int = 27
}