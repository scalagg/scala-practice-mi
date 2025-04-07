package gg.scala.practice.commands.menu.admin

import com.cryptomorin.xseries.XMaterial
import gg.scala.practice.commands.menu.admin.category.KitCategorySelectionMenu
import gg.scala.practice.commands.menu.admin.config.PracticeConfigurationMenu
import gg.scala.practice.commands.menu.admin.kit.KitEditorSelectionMenu
import gg.scala.practice.commands.menu.admin.map.MapEditorSelectionMenu
import gg.scala.practice.commands.menu.admin.replication.ReplicationManagementMenu
import gg.scala.practice.kit.KitService
import gg.scala.practice.profile.PracticeProfile
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player

class PracticeLobbyAdminMenu: Menu()
{
    init
    {
        placeholder = true
    }

    override fun getButtons(player: Player): Map<Int, Button>
    {
        return mutableMapOf<Int, Button>().also {

            it[4] = ItemBuilder.of(XMaterial.COMPASS)
                .name("${CC.B_RED}Practice Settings")
                .addToLore(
                    "${CC.GRAY}Edit all of the practice-specific",
                    "${CC.GRAY}settings that are currently",
                    "${CC.GRAY}available.",
                    "",
                    "${CC.GREEN}Click to configure!"
                ).toButton { _, _ ->
                    PracticeConfigurationMenu().openMenu(player)
                }

            it[10] = ItemBuilder.of(XMaterial.DIAMOND_SWORD)
                .name("${CC.B_GREEN}Edit Kits")
                .addToLore(
                    "${CC.GRAY}Edit all of the kits that",
                    "${CC.GRAY}are currently on the server.",
                    "",
                    "${CC.GREEN}Click to edit!"
                ).toButton { _, _ ->
                    KitEditorSelectionMenu(player).openMenu(player)
                }

            it[12] = ItemBuilder.of(XMaterial.MAP)
                .name("${CC.B_YELLOW}Edit Maps")
                .addToLore(
                    "${CC.GRAY}Edit all of the maps that",
                    "${CC.GRAY}are currently on the server.",
                    "",
                    "${CC.GREEN}Click to edit!"
                ).toButton { _, _ ->
                    MapEditorSelectionMenu().openMenu(player)
                }

            it[14] = ItemBuilder.of(XMaterial.HOPPER)
                .name("${CC.B_GREEN}Edit Kit Categories")
                .addToLore(
                    "${CC.GRAY}Edit all of the kit categories that",
                    "${CC.GRAY}are currently on the server.",
                    "",
                    "${CC.GREEN}Click to edit!"
                ).toButton { _, _ ->
                    KitCategorySelectionMenu().openMenu(player)
                }

            it[16] = ItemBuilder.of(XMaterial.MINECART)
                .name("${CC.BD_PURPLE}Replication Settings")
                .addToLore(
                    "${CC.GRAY}View and monitor all of the active",
                    "${CC.GRAY}replications currently in use",
                    "",
                    "${CC.GREEN}Click to view!"
                ).toButton { _, _ ->
                    ReplicationManagementMenu().openMenu(player)
                }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int = 27
    override fun getTitle(player: Player): String = "Select an Option"
}