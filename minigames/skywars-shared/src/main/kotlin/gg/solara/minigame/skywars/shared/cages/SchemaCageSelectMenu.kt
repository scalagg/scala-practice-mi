package gg.solara.minigame.skywars.shared.cages

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
class SchemaCageSelectMenu(
    private val select: (SchemaCage) -> Unit,
    private val previous: Menu
) : PaginatedMenu()
{
    override fun getAllPagesButtons(player: Player) = SchemaCageDataSync.cached().schemas.values
        .map {
            ItemBuilder
                .of(Material.MAP)
                .name("${CC.GREEN}${it.id}")
                .addToLore(
                    "${CC.GRAY}Click to select!"
                )
                .toButton { _, _ ->
                    Button.playNeutral(player)
                    select(it)
                    previous.openMenu(player)
                }
        }
        .withIndex()
        .associate { it.index to it.value }

    override fun getPrePaginatedTitle(player: Player) = "Select a schema..."
}
