package gg.scala.practice.menu

import gg.scala.practice.kit.category.KitCategory
import gg.scala.practice.kit.category.KitCategoryService
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemFlag

/**
 * @author Topu
 * @date 1/15/2025
 */
abstract class TemplateKitCategoryMenu : PaginatedMenu()
{
    init
    {
        placeholder = true
    }

    abstract fun filterDisplayOfCategory(map: KitCategory): Boolean
    abstract fun itemDescriptionOf(player: Player, kitCategory: KitCategory): List<String>
    abstract fun itemClicked(player: Player, kitCategory: KitCategory, type: ClickType)

    open fun itemTitleFor(player: Player, kitCategory: KitCategory): String
    {
        return "${CC.B_GREEN}${kitCategory.name}"
    }

    override fun size(buttons: Map<Int, Button>) = 45
    override fun getMaxItemsPerPage(player: Player) = 21

    override fun getAllPagesButtonSlots() =
        (10..16) + (19..25) + (28..34)

    override fun getPageButtonSlots() = 18 to 26

    private val filteredCategories by lazy {
        KitCategoryService
            .cached()
            .categories
            .filter {
                filterDisplayOfCategory(it)
            }
    }

    fun getAvailableMaps() = filteredCategories
    fun ensureMapsAvailable() = filteredCategories.isNotEmpty()

    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()

        filteredCategories.forEach {
            buttons[buttons.size] = ItemBuilder
                .copyOf(ItemBuilder.of(it.item))
                .name(itemTitleFor(player, it))
                .addFlags(
                    ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_ENCHANTS,
                    ItemFlag.HIDE_POTION_EFFECTS
                )
                .addToLore(
                    *itemDescriptionOf(player, it)
                        .toTypedArray()
                )
                .toButton { _, type ->
                    itemClicked(player, it, type!!)
                }
        }

        return buttons
    }
}
