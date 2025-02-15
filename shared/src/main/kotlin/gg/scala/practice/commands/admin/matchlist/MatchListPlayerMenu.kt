package gg.scala.practice.commands.admin.matchlist

import gg.scala.lemon.util.QuickAccess
import gg.scala.lemon.util.QuickAccess.username
import gg.scala.lemon.util.SplitUtil
import gg.scala.practice.games.GameReference
import gg.scala.practice.kit.KitService
import gg.scala.practice.map.MapService
import gg.scala.practice.queue.QueueType
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @author Topu
 * @date 2/2/2025
 */
class MatchListPlayerMenu(
    private val games: List<GameReference>
) : PaginatedMenu()
{
    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        var indexCounter = 0

        return games
            .map { reference ->
                ItemBuilder
                    .copyOf(KitService.cached().kits[reference.kitID]?.displayIcon ?: ItemStack(Material.BARRIER))
                    .name(
                        "${CC.PRI}${QuickAccess.nameOrConsole(reference.players.first())} vs ${QuickAccess.nameOrConsole(reference.players.last())}"
                    )
                    .addToLore(
                        "${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.WHITE} State: ${CC.PRI}${reference.state.name}",
                        "",
                        "${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.WHITE} Map: ${CC.PRI}${MapService.cached().maps[reference.mapID]?.displayName ?: MapService.cached().maps[reference.mapID]?.name}",
                        "${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.WHITE} Kit: ${CC.PRI}${KitService.cached().kits[reference.kitID]?.displayName ?: KitService.cached().kits[reference.kitID]?.id}",
                        "",
                        "${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.B_WHITE} Spectators:${
                            if (reference.spectators.isEmpty()) "${CC.RED} None!" else ""
                        }"
                    )
                    .apply {
                        if (reference.spectators.isNotEmpty())
                        {
                            reference.spectators.forEach { spectator ->
                                addToLore("  ${CC.GRAY}${Constants.SMALL_DOT_SYMBOL} ${CC.WHITE}${spectator.username()}")
                            }
                        }
                    }
                    .addToLore(
                        "",
                        "${CC.GREEN}(Click to spectate)",
                    )
                    .toButton { _, _ ->
                        player.performCommand(
                            "spectate ${reference.players.first().username()}"
                        )
                    }
            }
            .associateBy { indexCounter++ }
    }

    override fun getPrePaginatedTitle(player: Player) = "Viewing all games"
}
