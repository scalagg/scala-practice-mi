package gg.scala.practice.commands.menu.admin.map.groups

import com.cryptomorin.xseries.XMaterial
import gg.scala.practice.kit.group.KitGroupService
import gg.scala.practice.map.MapService
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player

/**
 * @author Topu
 * @date 4/7/2025
 */
class MapGroupEditorMenu(val map: gg.scala.practice.map.Map) : PaginatedMenu()
{

    init
    {
        placeholder = true
        updateAfterClick = true
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        return mutableMapOf<Int, Button>().also { map ->
            KitGroupService.cached().groups.forEach { group ->
                map[map.size] =
                    ItemBuilder.of(if (this.map.associatedKitGroups.contains(group.id)) XMaterial.GREEN_WOOL else XMaterial.RED_WOOL)
                        .name("${CC.B_YELLOW}${group.id}")
                        .setLore(mutableListOf("${CC.GRAY}Contains:") + this.map.associatedKitGroups.map { "${CC.GRAY}- ${CC.WHITE}$it" })
                        .toButton { _, _ ->
                            if (this.map.associatedKitGroups.contains(group.id))
                            {
                                this.map.associatedKitGroups.remove(group.id)
                            } else
                            {
                                this.map.associatedKitGroups.add(group.id)
                            }

                            MapService.sync(MapService.cached())
                        }
            }
        }
    }

//    override fun onClose(player: Player, manualClose: Boolean)
//    {
//        if (manualClose)
//        {
//            SpecificMapEditorMenu(map).openMenu(player)
//        }
//    }

    override fun getPrePaginatedTitle(player: Player): String
    {
        return "Select a Group..."
    }
}