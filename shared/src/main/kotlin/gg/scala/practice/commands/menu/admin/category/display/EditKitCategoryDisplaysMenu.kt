package gg.scala.practice.commands.menu.admin.category.display

import com.cryptomorin.xseries.XMaterial
import gg.scala.practice.commands.menu.admin.category.SpecificKitCategoryEditorMenu
import gg.scala.practice.kit.category.KitCategory
import gg.scala.practice.kit.category.KitCategoryService
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author Topu
 * @date 4/7/2025
 */
class EditKitCategoryDisplaysMenu(val kitCategory: KitCategory) : Menu()
{
    init
    {
        placeholder = true
    }

    override fun getTitle(player: Player): String
    {
        return "Edit Kit Category Displays"
    }

    override fun size(buttons: Map<Int, Button>): Int
    {
        return 27
    }

    override fun getButtons(player: Player): Map<Int, Button>
    {
        return mutableMapOf<Int, Button>().also { mutableMap ->
            mutableMap[4] = ItemBuilder.of(XMaterial.RED_BED)
                .name("${CC.B_RED}Go Back")
                .addToLore(
                    "${CC.GRAY}Go back to the kit editor",
                    "",
                    "${CC.GREEN}Click to go back!"
                ).toButton { _, _ ->
                    SpecificKitCategoryEditorMenu(kitCategory).openMenu(player)
                }

            mutableMap[11] = ItemBuilder.of(XMaterial.NAME_TAG)
                .name("${CC.B_GREEN}Change Name")
                .addToLore(
                    "${CC.GRAY}Change the name of this category",
                    "${CC.GRAY}that is seen when the player is",
                    "${CC.GRAY}selecting said category.",
                    "",
                    "${CC.GREEN}Click to change!"
                ).toButton { _, _ ->
                    InputPrompt()
                        .withText("${CC.GREEN}Enter the new name that you want this kit to have:")
                        .acceptInput { _, input ->
                            kitCategory.name = input
                            KitCategoryService.sync(KitCategoryService.cached())

                            EditKitCategoryDisplaysMenu(kitCategory).openMenu(player)
                        }.start(player)
                }

            mutableMap[13] = ItemBuilder.of(XMaterial.PAPER)
                .name("${CC.B_GREEN}Change Description")
                .addToLore(
                    "${CC.GRAY}Change the description of this category",
                    "${CC.GRAY}that is seen when the player is",
                    "${CC.GRAY}choosing the category or viewing areas",
                    "${CC.GRAY}with the category in it.",
                    "",
                    "${CC.GREEN}Click to change!"
                ).toButton { _, _ ->
                    Button.playNeutral(player)
                    object : TextEditorMenu(kitCategory.description)
                    {
                        override fun getPrePaginatedTitle(player: Player) =
                            "Editing: ${kitCategory.id} Description..."

                        override fun onClose(player: Player)
                        {

                        }

                        override fun onSave(player: Player, list: List<String>)
                        {
                            kitCategory.description.clear()
                            kitCategory.description.addAll(list)

                            KitCategoryService.sync(KitCategoryService.cached())

                            player.sendMessage("${CC.GREEN}Saved ${kitCategory.id} description!")
                            EditKitCategoryDisplaysMenu(kitCategory).openMenu(player)
                        }
                    }.openMenu(player)
                }

            mutableMap[15] = ItemBuilder.of(XMaterial.STICK)
                .name("${CC.B_GREEN}Change Icon")
                .addToLore(
                    "${CC.GRAY}Change the icon of this category",
                    "${CC.GRAY}that is seen when the player is",
                    "${CC.GRAY}selecting said category.",
                    "",
                    "${CC.GREEN}Click to change!"
                ).toButton { _, _ ->
                    InputPrompt()
                        .withText("${CC.GREEN}Enter the new item that you want this category's icon to be:")
                        .acceptInput { _, input ->
                            val item = Material.values().firstOrNull { it.name.equals(input, ignoreCase = true) }

                            if (item == null)
                            {
                                player.sendMessage("${CC.RED}This is an invalid material.")
                                return@acceptInput
                            }

                            kitCategory.item = item
                            KitCategoryService.sync(KitCategoryService.cached())

                            EditKitCategoryDisplaysMenu(kitCategory).openMenu(player)
                        }.start(player)
                }
        }
    }
}