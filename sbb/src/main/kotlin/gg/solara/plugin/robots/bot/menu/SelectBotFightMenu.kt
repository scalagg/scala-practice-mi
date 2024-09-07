package gg.solara.plugin.robots.bot.menu

import gg.solara.plugin.robots.commands.UnrankedAdminCommand
import gg.tropic.practice.player.hotbar.LobbyHotbarService.buildNewBotFightMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author TastyCake
 * @date 9/7/2024
 */
class SelectBotFightMenu : Menu("Select a bot fight...")
{
    init
    {
        placeholder = true
    }

    override fun getButtons(player: Player): Map<Int, Button> =
    mutableMapOf<Int, Button>().apply {
        this[13] = ItemBuilder
            .of(Material.GOLD_BLOCK)
            .name("${CC.GOLD}Practice")
            .addToLore("${CC.GRAY}") // TODO: Add desc
            .toButton { player, _ ->
                if (player!!.hasMetadata("frozen"))
                {
                    player.sendMessage("${CC.RED}You cannot join queues while frozen!")
                    return@toButton
                }

                val menu = buildNewBotFightMenu(player)
                    ?: return@toButton run {
                        player.sendMessage("${CC.RED}This feature is coming soon!")
                    }

                menu.openMenu(player)
            }

        this[15] = ItemBuilder
            .of(Material.GOLD_BLOCK)
            .name("${CC.GOLD}Unranked")
            .addToLore("${CC.GRAY}") // TODO: Add desc
            .toButton { player, _ ->
                if (player!!.hasMetadata("frozen"))
                {
                    player.sendMessage("${CC.RED}You cannot join queues while frozen!")
                    return@toButton
                }

                if (!UnrankedAdminCommand.available) {
                    player.sendMessage("${CC.RED}This feature is unavaliable!")
                    return@toButton
                }

                UnrankedBotFightMenu(player)
            }
    }

    override fun size(buttons: Map<Int, Button>): Int = 27
}