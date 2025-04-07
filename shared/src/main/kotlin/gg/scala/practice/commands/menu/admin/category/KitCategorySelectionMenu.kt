package gg.scala.practice.commands.menu.admin.category

import gg.scala.practice.kit.category.KitCategory
import gg.scala.practice.menu.TemplateKitCategoryMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

/**
 * @author Topu
 * @date 4/7/2025
 */
class KitCategorySelectionMenu() : TemplateKitCategoryMenu()
{
    override fun filterDisplayOfCategory(kitCategory: KitCategory) = true

    override fun itemDescriptionOf(player: Player, kitCategory: KitCategory) = listOf(
        if (kitCategory.newCategory) "${CC.GREEN}Category is new..." else "${CC.RED}Category is not new...",
        "",
        "${CC.GRAY}Identifier: ${CC.WHITE}${kitCategory.id}",
        "${CC.GRAY}Display Name: ${CC.WHITE}${kitCategory.name}",
        "",
        "${CC.YELLOW}Kits:",
        *kitCategory.kits
            .map { "${CC.WHITE}- $it" }
            .toTypedArray(),
        "",
        "${CC.GREEN}Click to manage this category!"
    )

    override fun itemClicked(player: Player, kitCategory: KitCategory, type: ClickType)
    {
        Button.playNeutral(player)
        SpecificKitCategoryEditorMenu(kitCategory).openMenu(player)
    }

    override fun getPrePaginatedTitle(player: Player) = "Kit Category Administration"
}
