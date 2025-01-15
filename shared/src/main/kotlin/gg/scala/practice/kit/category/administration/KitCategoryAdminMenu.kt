package gg.scala.practice.kit.category.administration

import gg.scala.practice.kit.category.KitCategory
import gg.scala.practice.kit.category.administration.specific.SpecificKitCategoryAdminMenu
import gg.scala.practice.map.Map
import gg.scala.practice.map.administration.specific.SpecificMapAdminMenu
import gg.scala.practice.menu.TemplateKitCategoryMenu
import gg.scala.practice.menu.TemplateMapMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

/**
 * @author Topu
 * @date 1/15/2025
 */
class KitCategoryAdminMenu : TemplateKitCategoryMenu()
{
    override fun filterDisplayOfCategory(kitCategory: KitCategory) = true
    override fun itemDescriptionOf(player: Player, kitCategory: KitCategory) = listOf(
        "${CC.GRAY}ID: ${CC.WHITE}${kitCategory.id}",
        "${CC.GRAY}Name: ${CC.WHITE}${kitCategory.name}",
        "",
        "${CC.YELLOW}Description: ${CC.WHITE}",
        *kitCategory.description
            .map { "${CC.WHITE}- $it" }
            .toTypedArray(),
        "",
        "${CC.GRAY}New Category: ${
            if (kitCategory.newCategory) "${CC.D_GREEN}✔" else "${CC.D_RED}✘"
        }",
        "",
        "${CC.GREEN}Click to manage!"
    )

    override fun itemClicked(player: Player, kitCategory: KitCategory, type: ClickType)
    {
        Button.playNeutral(player)
        SpecificKitCategoryAdminMenu(kitCategory).openMenu(player)
    }

    override fun getPrePaginatedTitle(player: Player) = "Kit Category Administration"
}