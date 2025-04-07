package gg.scala.practice.commands.menu.admin.category

import com.cryptomorin.xseries.XMaterial
import gg.scala.practice.kit.KitService
import gg.scala.practice.kit.category.KitCategory
import gg.scala.practice.kit.category.KitCategoryService
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player

/**
 * @author Topu
 * @date 4/7/2025
 */
class KitCategoryEditorMenu(val kitCategory: KitCategory): PaginatedMenu()
{

    init
    {
        placeholder = true
        updateAfterClick = true
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        return mutableMapOf<Int, Button>().also { map ->
            KitService.cached().kits.values.forEach { kit ->
                map[map.size] = ItemBuilder.of(if (kitCategory.kits.contains(kit.id)) XMaterial.GREEN_WOOL else XMaterial.RED_WOOL)
                    .name("${CC.B_YELLOW}${kit.id}")
                    .toButton { _, _ ->
                        if (kitCategory.kits.contains(kit.id))
                        {
                            kitCategory.kits.remove(kit.id)
                        } else
                        {
                            kitCategory.kits.add(kit.id)
                        }

                        KitCategoryService.sync(KitCategoryService.cached())
                    }
            }
        }
    }

    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (manualClose)
        {
            SpecificKitCategoryEditorMenu(kitCategory).openMenu(player)
        }
    }

    override fun getPrePaginatedTitle(player: Player): String
    {
        return "Select a Kit..."
    }
}