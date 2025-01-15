package gg.scala.practice.kit.category.administration.specific

import gg.scala.practice.kit.KitService
import gg.scala.practice.kit.category.KitCategory
import gg.scala.practice.kit.category.KitCategoryConfigModel
import gg.scala.practice.kit.category.KitCategoryService
import gg.scala.practice.kit.category.administration.KitCategoryAdminMenu
import gg.scala.practice.map.MapService
import gg.scala.practice.map.administration.MapAdminMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author Topu
 * @date 1/15/2025
 */
class SpecificKitCategoryAdminMenu(private val kitCategory: KitCategory) : Menu(
    "Editing ${kitCategory.name}..."
)
{
    override fun getButtons(player: Player) = mapOf(
        1 to ItemBuilder
            .of(Material.NETHER_STAR)
            .name("${CC.RED}New Category")
            .addToLore(
                "${CC.GRAY}Is the category new?",
                if (kitCategory.newCategory) "${CC.GREEN}Yes" else "${CC.RED}No"
            )
            .toButton { _, _ ->
                Button.playSuccess(player)
                with(KitCategoryService.cached()) {
                    kitCategory.newCategory = !kitCategory.newCategory
                    KitCategoryService.sync(this)
                }
            },

        3 to ItemBuilder
            .of(Material.NAME_TAG)
            .name("${CC.RED}Display Name")
            .addToLore(
                "${CC.GRAY}The display name of the category.",
            )
            .toButton { _, _ ->
                Button.playSuccess(player)
                InputPrompt()
                    .withText("${CC.YELLOW}Ender the new Display Name:")
                    .acceptInput { _, text ->

                        kitCategory.name = text
                        with(KitCategoryService.cached()) {
                            kitCategory.name = text
                            KitCategoryService.sync(this)
                        }

                        player.sendMessage(
                            "${CC.GREEN}Kit Category ${CC.YELLOW}${text}${CC.GREEN} is now displayed as ${text}${CC.GREEN}."
                        )
                    }
            },

        5 to ItemBuilder
            .of(Material.PAPER)
            .name("${CC.RED}Description")
            .addToLore(
                "${CC.GRAY}The description of the category.",
            )
            .toButton { _, _ ->
                Button.playSuccess(player)
                InputPrompt()
                    .withText("${CC.YELLOW}Enter the new Description:")
                    .acceptInput { _, text ->

                        kitCategory.description = text.split("\n").toMutableList()
                        with(KitCategoryService.cached()) {
                            kitCategory.description = text.split("\n").toMutableList()
                            KitCategoryService.sync(this)
                        }

                        player.sendMessage(
                            "${CC.GREEN}Kit Category ${CC.YELLOW}${kitCategory.name}${CC.GREEN} now has a new description."
                        )
                    }
            },
    )

    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (manualClose)
        {
            Tasks.delayed(1L) {
                KitCategoryAdminMenu().openMenu(player)
            }
        }
    }
}
