package gg.solara.plugin.robots.bot.menu

import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import gg.solara.plugin.robots.bot.menu.profiles.PlayerProfileBackbone
import gg.solara.plugin.robots.bot.menu.profiles.ProfileCategoryMenu
import gg.solara.plugin.robots.bot.menu.profiles.RobotProfileBackbone
import gg.solara.plugin.robots.bot.profile.RobotProfile
import gg.solara.plugin.robots.bot.profile.RobotSettingProfile
import gg.solara.plugin.robots.bot.profile.UnrankedBotsService
import gg.solara.plugin.robots.bot.setting.setting.persistence.SettingProfile
import gg.solara.plugin.robots.bot.setting.setting.root
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

/**
 * @author TastyCake
 * @date 8/29/2024
 */
class UnrankedAdminMenu : PaginatedMenu()
{
    companion object
    {
        val slots = mutableListOf(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            19 + 9, 20 + 9, 21 + 9, 22 + 9, 23 + 9, 24 + 9, 25 + 9,
        )
    }

    init
    {
        placeholdBorders = true
    }

    override fun getGlobalButtons(player: Player) =
        mutableMapOf<Int, Button>()
            .apply {
                this[4] = ItemBuilder
                    .of(Material.EMERALD)
                    .name("${CC.GREEN}Create new profile")
                    .addToLore(
                        "${CC.GRAY}Click to create new profile!"
                    )
                    .toButton { player, _ ->
                        val id = "default${buttons.size + 1}"
                        val settings = SettingProfile(id)
                        UnrankedBotsService.bots[id] = RobotSettingProfile(UUID.randomUUID(), settings)
                        UnrankedBotsService.save(id, settings)
                        Button.playSuccess(player!!)
                        openMenu(player)
                    }
            }

    override fun getAllPagesButtons(player: Player) =
        mutableMapOf<Int, Button>()
            .apply {
                var i = 0
                UnrankedBotsService.bots.values.forEach {
                    this[i++] = ItemBuilder
                        .of(Material.PAPER)
                        .name(it.settingProfile.id)
                        .addToLore(
                            "${CC.GREEN}Click to edit",
                            "${CC.RED}Right Click to remove"
                        )
                        .toButton { player, clickType ->
                            if (clickType!!.isRightClick) {
                                UnrankedBotsService.bots.remove(it.settingProfile.id)
                                Button.playSuccess(player!!)
                                UnrankedAdminMenu().openMenu(player)
                                DataStoreObjectControllerCache
                                    .findNotNull<RobotSettingProfile>()
                                    .delete(it.identifier, DataStoreStorageType.MONGO)
                                return@toButton
                            }

                            Button.playNeutral(player!!)
                            ProfileCategoryMenu(it.settingProfile, RobotProfileBackbone(it.settingProfile.id), root).openMenu(player)
                            return@toButton
                        }
                }
            }

    override fun getAllPagesButtonSlots(): List<Int> = slots

    override fun getPrePaginatedTitle(player: Player) = "Unranked Admin"

    override fun size(buttons: Map<Int, Button>) = 45
    override fun getMaxItemsPerPage(player: Player) = 25
}