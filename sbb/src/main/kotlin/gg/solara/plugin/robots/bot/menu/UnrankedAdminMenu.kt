package gg.solara.plugin.robots.bot.menu

import gg.solara.plugin.robots.bot.menu.profiles.PlayerProfileBackbone
import gg.solara.plugin.robots.bot.menu.profiles.ProfileCategoryMenu
import gg.solara.plugin.robots.bot.profile.UnrankedBotsService
import gg.solara.plugin.robots.bot.setting.setting.persistence.SettingProfile
import gg.solara.plugin.robots.bot.setting.setting.root
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author TastyCake
 * @date 8/29/2024
 */
class UnrankedAdminMenu : PaginatedMenu()
{
    override fun getGlobalButtons(player: Player) =
        mutableMapOf<Int, Button>()
            .apply {
                this[5] = ItemBuilder
                    .of(Material.EMERALD)
                    .name("${CC.GREEN}Create new profile")
                    .addToLore(
                        "${CC.GRAY}Click to create new profile!"
                    )
                    .toButton { player, _ ->
                        UnrankedBotsService.bots.add(SettingProfile("default${buttons.size + 1}"))
                        UnrankedBotsService.save()
                        Button.playSuccess(player!!)
                        openMenu(player)
                    }
            }

    override fun getAllPagesButtons(player: Player) =
        mutableMapOf<Int, Button>()
            .apply {
                UnrankedBotsService.bots.forEach {
                    this[buttons.size] = ItemBuilder
                        .of(Material.PAPER)
                        .name(it.id)
                        .addToLore(
                            "${CC.GREEN}Click to edit",
                            "${CC.RED}Right Click to remove"
                        )
                        .toButton { player, clickType ->
                            if (clickType!!.isRightClick) {
                                UnrankedBotsService.bots.removeIf { i -> it.id == i.id }
                                Button.playSuccess(player!!)
                                openMenu(player)
                                UnrankedBotsService.save()
                                return@toButton
                            }

                            Button.playNeutral(player!!)
                            ProfileCategoryMenu(UnrankedBotsService.bots.first { i -> it.id == i.id }, PlayerProfileBackbone(player), root).openMenu(player)
                            return@toButton
                        }
                }
            }

    override fun getPrePaginatedTitle(player: Player) = "Unranked Admin"

    override fun size(buttons: Map<Int, Button>) = 45
    override fun getMaxItemsPerPage(player: Player) = 36
}