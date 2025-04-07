package gg.scala.practice.commands.menu.admin.category

import com.cryptomorin.xseries.XMaterial
import gg.scala.practice.commands.menu.admin.category.display.EditKitCategoryDisplaysMenu
import gg.scala.practice.commands.menu.rating.MapRatingOverviewMenu
import gg.scala.practice.kit.category.KitCategory
import gg.scala.practice.kit.category.KitCategoryService
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player

/**
 * @author Topu
 * @date 4/7/2025
 */
class SpecificKitCategoryEditorMenu(val kitCategory: KitCategory): Menu()
{
    init
    {
        placeholder = true
    }

    override fun size(buttons: Map<Int, Button>): Int
    {
        return 36
    }

    override fun getTitle(player: Player): String
    {
        return "Editing: ${kitCategory.name}"
    }

    override fun getButtons(player: Player): Map<Int, Button>
    {
        return mutableMapOf<Int, Button>().also {
            it[12] = ItemBuilder.of(XMaterial.RED_WOOL)
                .name("${CC.B_RED}Delete Category")
                .addToLore(
                    "${CC.GRAY}Delete this category from the server",
                    "",
                    "${CC.GREEN}Click to delete!"
                ).toButton { _, _ ->
                    with (KitCategoryService.cached()) {
                        this.categories.remove(kitCategory)
                        KitCategoryService.sync(this)
                    }

                    player.sendMessage("${CC.RED}You have deleted this map from the server!")
                    KitCategorySelectionMenu().openMenu(player)
                }

            it[13] = ItemBuilder.of(kitCategory.item)
                .name("${CC.B_YELLOW}${kitCategory.name}")
                .addToLore(
                    "${CC.GRAY}You are currently in the management menu",
                    "${CC.GRAY}for the category ${CC.WHITE}${kitCategory.name}${CC.GRAY}.",
                ).toButton()

            it[4] = ItemBuilder.of(XMaterial.RED_BED)
                .name("${CC.B_RED}Go Back")
                .addToLore(
                    "${CC.GRAY}Go back to the kit category editor",
                    "${CC.GRAY}selection menu.",
                    "",
                    "${CC.GREEN}Click to go back!"
                ).toButton { _, _ ->
                    KitCategorySelectionMenu().openMenu(player)
                }

            it[14] = ItemBuilder.of(XMaterial.FIRE_CHARGE)
                .name("${CC.B_YELLOW}Edit Current Kits")
                .addToLore(
                    "${CC.GRAY}Edit the specific kits that",
                    "${CC.GRAY}this category tailors to.",
                    "",
                    "${CC.GREEN}Click to edit!"
                ).toButton { _, _ ->
                    KitCategoryEditorMenu(kitCategory).openMenu(player)
                }

            it[22] = ItemBuilder.of(XMaterial.NAME_TAG)
                .name("${CC.BD_PURPLE}Edit Display Properties")
                .addToLore(
                    "${CC.GRAY}Edit the display properties of",
                    "${CC.GRAY}this map. This includes traits",
                    "${CC.GRAY}such as display name, description,",
                    "${CC.GRAY}and icon.",
                    "",
                    "${CC.GREEN}Click to edit!"
                ).toButton { _, _ ->
                    EditKitCategoryDisplaysMenu(kitCategory).openMenu(player)
                }
        }
    }
}