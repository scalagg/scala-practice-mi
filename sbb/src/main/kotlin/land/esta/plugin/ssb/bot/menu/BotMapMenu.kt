package land.esta.plugin.ssb.bot.menu

import gg.tropic.practice.kit.Kit
import gg.tropic.practice.kit.group.KitGroupService
import gg.tropic.practice.map.Map
import gg.tropic.practice.menu.TemplateMapMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

/**
 * @author GrowlyX
 * @since 8/9/2024
 */
class BotMapMenu(private val kit: Kit, private val selectionCallback: (Map?) -> Unit) : TemplateMapMenu()
{
    override fun filterDisplayOfMap(map: Map) = map.associatedKitGroups
        .intersect(KitGroupService.groupsOf(kit).map { it.id }.toSet())
        .isNotEmpty()

    override fun itemDescriptionOf(player: Player, map: Map) = listOf(
        "",
        "${CC.GREEN}Click to select!"
    )

    override fun itemClicked(player: Player, map: Map, type: ClickType)
    {
        Button.playNeutral(player)
        selectionCallback(map)
    }

    override fun getGlobalButtons(player: Player) = mutableMapOf(
        4 to ItemBuilder
            .of(Material.NETHER_STAR)
            .name("${CC.B_AQUA}Random Map")
            .addToLore(
                "",
                "${CC.AQUA}Click to select!"
            )
            .toButton { _, _ ->
                selectionCallback(null)
            }
    )

    override fun getPrePaginatedTitle(player: Player) = "Select a map..."

    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (manualClose)
        {
            Tasks.sync {
                selectionCallback(null)
            }
        }
    }
}
