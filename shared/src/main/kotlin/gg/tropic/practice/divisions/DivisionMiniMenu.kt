package gg.tropic.practice.divisions

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author TastyCake
 * @date 9/7/2024
 */
class DivisionMiniMenu(
    var similar: String
) : Menu(similar)
{
    init
    {
        placeholdBorders = true
    }

    override fun size(buttons: Map<Int, Button>): Int = 27
    override fun getButtons(player: Player): Map<Int, Button> =
        mutableMapOf<Int, Button>().apply {
            var i = 10

            Divisions.entries
                .filter { it.id.contains(similar) }
                .forEach { div ->
                    this[i++] = ItemBuilder
                        .of(Material.PAPER)
                        .name(div.title)
                        .addToLore(
                            "${CC.GRAY}Required wins: ${div.requiredWins}"
                        )
                        .toButton()
                }
        }


    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (manualClose) {
            Tasks
                .delayed(5) {
                    DivisionMenu().openMenu(player)
                }
        }
    }
}