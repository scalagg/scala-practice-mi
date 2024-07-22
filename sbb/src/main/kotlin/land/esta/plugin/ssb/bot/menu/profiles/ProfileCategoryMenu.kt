package land.esta.plugin.ssb.bot.menu.profiles

import land.esta.plugin.ssb.bot.setting.setting.Category
import land.esta.plugin.ssb.bot.setting.setting.Option
import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile
import land.esta.plugin.ssb.bot.setting.setting.root
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.math.ceil

/**
 * @author GrowlyX
 * @since 7/22/2024
 */
class ProfileCategoryMenu(
    private val profile: SettingProfile,
    private val backbone: ProfileBackbone,
    private val category: Category
) : PaginatedMenu()
{
    init
    {
        placeholder = true
    }

    private fun Int.toRow() = (1..7).map { it + this * 9 }
    override fun getAllPagesButtonSlots() = (1..4).flatMap { it.toRow() }

    override fun getGlobalButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()
        if (category != root)
        {
            return buttons
        }

        buttons[3] = ItemBuilder
            .of(Material.NAME_TAG)
            .name("${CC.B_GOLD}Name")
            .addToLore(
                "${CC.GRAY}Your bot's name is currently:",
                "${CC.WHITE}${profile.botName}",
                "",
                "${CC.GOLD}Click to change!"
            )
            .toButton { _, _ ->
                Button.playNeutral(player)
                player.closeInventory()

                InputPrompt()
                    .withText("${CC.GREEN}Enter a new bot name!")
                    .acceptInput { _, text ->
                        profile.botName = text
                        backbone.save()

                        player.sendMessage("${CC.GREEN}Saved!")
                        ProfileCategoryMenu(profile, backbone, category).openMenu(player)
                    }
                    .start(player)
            }

        buttons[5] = ItemBuilder
            .copyOf(
                object : TexturedHeadButton(profile.skinProperties.value) { }
                    .getButtonItem(player)
            )
            .name("${CC.BD_AQUA}Skin")
            .addToLore(
                "",
                "${CC.D_AQUA}Click to change!"
            )
            .toButton { _, _ ->
                Button.playNeutral(player)
                player.closeInventory()

                InputPrompt()
                    .withText("${CC.GREEN}Enter a new bot name!")
                    .acceptInput { _, text ->
                        // TODO
                    }
                    .start(player)
            }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>) = 18 + 9 * ceil(category.children.values.size / 7.0).toInt()
    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()
        for (setting in category.children.values)
        {
            buttons[buttons.size] = ItemBuilder
                .copyOf(
                    setting.toItemStack(player, profile)
                )
                .toButton { _, clickType ->
                    if (setting.permission != null)
                    {
                        if (!player.hasPermission(setting.permission))
                        {
                            Button.playFail(player)
                            player.sendMessage("${CC.RED}You do not have permission to use this!")
                            return@toButton
                        }
                    }

                    if (setting is Category)
                    {
                        Button.playNeutral(player)
                        ProfileCategoryMenu(profile, backbone, setting).openMenu(player)
                        return@toButton
                    }

                    if (setting is Option<*>)
                    {
                        Button.playSuccess(player)
                        setting.interact(player, profile, backbone, clickType!!)

                        openMenu(player)
                    }
                }
        }

        return buttons
    }

    override fun getPrePaginatedTitle(player: Player) = category.displayName
    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (category.parent == null)
        {
            return
        }

        Tasks.sync {
            ProfileCategoryMenu(profile, backbone, category.parent!!).openMenu(player)
        }
    }
}
