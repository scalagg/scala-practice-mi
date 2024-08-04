package land.esta.plugin.ssb.bot.menu

import com.cryptomorin.xseries.XMaterial
import land.esta.plugin.ssb.bot.menu.profiles.PlayerDescribeProfilesMenu
import land.esta.plugin.ssb.bot.menu.profiles.PlayerProfileBackbone
import land.esta.plugin.ssb.bot.menu.profiles.ProfileCategoryMenu
import land.esta.plugin.ssb.bot.profile.RobotProfileService
import land.esta.plugin.ssb.bot.setting.BotSettingService
import land.esta.plugin.ssb.bot.setting.setting.root
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
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
        placeholdBorders = true
    }

    fun Player.buildNewItem(id: String, display: String, data: Short, skinBased: Boolean = false): Button
    {
        val profile = BotSettingService.loadDefaultProfile(id)

        return (if (skinBased) ItemBuilder
            .copyOf(object : TexturedHeadButton(profile.skinProperties.value)
            {}.getButtonItem(player))
            .data(3)
        else
            ItemBuilder
                .of(Material.WOOL)
                .data(data)
            ).name(display)
            .setLore(profile.describe())
            .addToLore(
                "",
                "${CC.GREEN}Click to play!"
            )
            .toButton { _, _ ->
                BotKitMenu(id).openMenu(this)
            }
    }

    override fun size(buttons: Map<Int, Button>): Int = 36
    override fun getButtons(player: Player): Map<Int, Button> = mapOf(
        10 to player.buildNewItem("EASY", "${CC.GREEN}Easy", 5),
        11 to player.buildNewItem("NORMAL", "${CC.YELLOW}Normal", 4),
        12 to player.buildNewItem("HARD", "${CC.RED}Hard", 14),
        13 to player.buildNewItem("EXPERT", "${CC.D_RED}Expert", 14),
        14 to player.buildNewItem("NIGHTMARE", "${CC.DARK_PURPLE}Nightmare", 10),

        17 to ItemBuilder
            .of(Material.AIR)
            .toButton(),

        26 to ItemBuilder
            .of(Material.AIR)
            .toButton(),

        19 to player.buildNewItem("CLOWN", "${CC.PINK}Clown", 6, true),
        20 to player.buildNewItem("MARTIAN", "${CC.GOLD}Martian", 12, true),

        9 to ItemBuilder
            .of(XMaterial.LIME_STAINED_GLASS_PANE)
            .name("&aNormal")
            .toButton(),

        18 to ItemBuilder
            .of(XMaterial.ORANGE_STAINED_GLASS_PANE)
            .name("&6Special")
            .toButton(),

        4 to ItemBuilder.of(Material.EMERALD)
            .name("&2Custom")
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
