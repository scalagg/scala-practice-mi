package land.esta.plugin.ssb.bot.menu

import land.esta.plugin.ssb.bot.menu.profiles.PlayerDescribeProfilesMenu
import land.esta.plugin.ssb.bot.menu.profiles.PlayerProfileBackbone
import land.esta.plugin.ssb.bot.menu.profiles.ProfileCategoryMenu
import land.esta.plugin.ssb.bot.profile.RobotProfileService
import land.esta.plugin.ssb.bot.setting.BotSettingService
import land.esta.plugin.ssb.bot.setting.setting.root
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

/**
 * Class created on 7/19/2024

 * @author Max C.
 * @project esta-practice
 * @website https://solo.to/redis
 */
class BotDuelMenu : Menu("Select a Difficulty...")
{
    init
    {
        placeholder = true
    }

    fun Player.buildNewItem(id: String, display: String, data: Short) = ItemBuilder.of(Material.WOOL)
        .data(data)
        .name(display)
        .setLore(BotSettingService.loadDefaultProfile(id).describe())
        .addToLore(
            "",
            "${CC.GREEN}Click to start an $display${CC.GREEN} match!"
        )
        .toButton { _, _ ->
            BotKitMenu(id).openMenu(this)
        }

    override fun size(buttons: Map<Int, Button>): Int = 27
    override fun getButtons(player: Player): Map<Int, Button> = mapOf(
        10 to player.buildNewItem("EASY", "${CC.GREEN}Easy", 5),
        11 to player.buildNewItem("NORMAL", "${CC.YELLOW}Normal", 4),
        12 to player.buildNewItem("HARD", "${CC.RED}Hard", 14),
        13 to player.buildNewItem("EXPERT", "${CC.D_RED}Expert", 14),
        14 to player.buildNewItem("NIGHTMARE", "${CC.DARK_PURPLE}Nightmare", 10),
        16 to ItemBuilder.of(Material.NETHER_STAR)
            .name("&6&lCustom")
            .addToLore(
                "${CC.GRAY}Available for ${CC.D_AQUA}VIP${CC.GRAY} and up!",
                "",
                "${CC.GREEN}Left-Click to play!",
                "${CC.GOLD}Right-Click to customize!"
            )
            .toButton { _, type ->
                if (!player.hasPermission("botgui.use"))
                {
                    Button.playFail(player)
                    player.sendMessage("${CC.RED}You cannot use this feature!")
                    return@toButton
                }

                val profile = RobotProfileService.find(player)
                    ?: return@toButton

                if (type == ClickType.RIGHT)
                {
                    Button.playNeutral(player)
                    PlayerDescribeProfilesMenu(profile) {
                        ProfileCategoryMenu(it, PlayerProfileBackbone(player), root).openMenu(player)
                    }.openMenu(player)
                    return@toButton
                }

                Button.playNeutral(player)
                PlayerDescribeProfilesMenu(profile) {
                    BotKitMenu(player.uniqueId.toString(), it.id).openMenu(player)
                }.openMenu(player)
            },
    )
}
