package gg.scala.practice.menu.manage

import com.cryptomorin.xseries.XMaterial
import gg.scala.practice.kit.administration.KitAdminMenu
import gg.scala.practice.kit.category.administration.KitCategoryAdminMenu
import gg.scala.practice.map.administration.MapAdminMenu
import gg.scala.practice.menu.manage.settings.PracticeSettingsMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player

/**
 * @author Topu
 * @date 1/15/2025
 */
class PracticeManagementMenu : Menu("Practice Administration")
{

    init
    {
        updateAfterClick = true
        placeholder = true
    }

    override fun size(buttons: Map<Int, Button>) = 27


    override fun getButtons(player: Player) = mapOf(
        10 to ItemBuilder
            .of(XMaterial.MAP)
            .name(
                "${CC.B_GREEN}Maps"
            )
            .addToLore(
                "${CC.GRAY}Edit maps names, locations,",
                "${CC.GRAY}icons, and more",
                "",
                "${CC.YELLOW}Click to open"
            )
            .toButton { _, _ ->
                MapAdminMenu().openMenu(player)
                Button.playNeutral(player)
            },

        12 to ItemBuilder
            .of(XMaterial.DIAMOND_SWORD)
            .name(
                "${CC.B_GREEN}Kits"
            )
            .addToLore(
                "${CC.GRAY}Edit kits names, locations,",
                "${CC.GRAY}icons, and more",
                "",
                "${CC.YELLOW}Click to open"
            )
            .toButton { _, _ ->
                KitAdminMenu(player).openMenu(player)
                Button.playNeutral(player)
            },

        14 to ItemBuilder
            .of(XMaterial.EMERALD)
            .name("${CC.B_GREEN}Kit Categories")
            .addToLore(
                "${CC.GRAY}Edit kit categories names, locations,",
                "${CC.GRAY}icons, and more",
                "",
                "${CC.YELLOW}Click to open"
            )
            .toButton { _, _ ->
                KitCategoryAdminMenu().openMenu(player)
                Button.playNeutral(player)
            },

        16 to ItemBuilder
            .of(XMaterial.COMPASS)
            .name("${CC.B_GREEN}Practice Settings")
            .addToLore(
                "${CC.GRAY}Edit all practice settings",
                "",
                "${CC.YELLOW}Click to open"
            )
            .toButton { _, _ ->
                PracticeSettingsMenu().openMenu(player)
                Button.playNeutral(player)
            },
    )
}