package gg.scala.practice.minigame.skywars.shared.cages

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
class SchemaCageSelectMenu(
    private val previous: Menu?,
    private val select: (SchemaCage) -> Unit,
) : PaginatedMenu()
{
    override fun getGlobalButtons(player: Player) = mapOf(
        4 to ItemBuilder
            .of(Material.PAPER)
            .name("${CC.WHITE}Default")
            .toButton { _, _ ->
                select(SchemaCageDefaultSchemas.DEFAULT)
            }
    )

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
                    previous?.openMenu(player)
                }
        }
        .withIndex()
        .associate { it.index to it.value }

    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (manualClose)
        {
            Tasks.sync { select(SchemaCageDefaultSchemas.DEFAULT) }
        }
    }

    override fun getPrePaginatedTitle(player: Player) = "Select a schema..."
}
