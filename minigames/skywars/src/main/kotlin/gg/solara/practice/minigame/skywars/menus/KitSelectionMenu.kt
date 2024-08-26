package gg.solara.practice.minigame.skywars.menus

import com.cryptomorin.xseries.XMaterial
import gg.scala.commons.scheme.impl.SinglePageSchemedMenu
import gg.solara.practice.minigame.skywars.shared.kits.SkyWarsKitRegistry
import gg.solara.practice.minigame.skywars.shared.profile.SkyWarsProfileService
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
object KitSelectionMenu
{
    val schema = SinglePageSchemedMenu()
        .title("Select a kit!")
        .pattern(
            "XXXXXXXXX",
            "X0000000X",
            "X0000000X",
            "XXXXXXXXX"
        )
        .map('X') { player, _ ->
            ItemBuilder
                .of(XMaterial.BLACK_STAINED_GLASS_PANE)
                .name(" ")
                .toButton()
        }
        .map('0') { player, index ->
            val kit = SkyWarsKitRegistry.kits.values
                .toList().getOrNull(index - 10)
                ?: return@map ItemBuilder
                    .of(XMaterial.BLACK_STAINED_GLASS_PANE)
                    .name(" ")
                    .toButton()

            ItemBuilder
                .copyOf(kit.icon)
                .name("${CC.GREEN}${kit.name}")
                .addToLore("${CC.GRAY}Click to select!")
                .toButton { _, _ ->
                    val profile = SkyWarsProfileService.find(player)
                        ?: return@toButton

                    Button.playNeutral(player)

                    profile.selectedKit = kit.name
                    profile.save()

                    player.sendMessage("${CC.GREEN}You selected the ${CC.AQUA}${kit.name}${CC.GREEN} kit!")
                    player.closeInventory()
                }
        }
        .compose()

}
