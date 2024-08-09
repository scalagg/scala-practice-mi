package land.esta.plugin.ssb.bot.menu.profiles

import land.esta.plugin.ssb.bot.profile.RobotProfile
import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

/**
 * @author GrowlyX
 * @since 7/22/2024
 */
class PlayerDescribeProfilesMenu(
    private val robotProfile: RobotProfile,
    private val action: (SettingProfile) -> Unit
) : PaginatedMenu()
{
    init
    {
        placeholder = true
    }

    private fun Int.toRow() = (1..7).map { it + this * 9 }

    override fun getAllPagesButtonSlots() = 1.toRow()
    override fun getMaxItemsPerPage(player: Player) = getAllPagesButtonSlots().size

    override fun size(buttons: Map<Int, Button>) = 27
    override fun getGlobalButtons(player: Player) = mutableMapOf(
        4 to ItemBuilder
            .of(Material.EMERALD)
            .name("${CC.BD_GREEN}New Profile")
            .addToLore(
                "",
                "${CC.D_GREEN}Click to create!"
            )
            .toButton { _, _ ->
                Button.playNeutral(player)
                player.closeInventory()

                InputPrompt()
                    .withText("${CC.GREEN}Enter a profile name!")
                    .acceptInput { _, newName ->
                        val sanitized = newName.lowercase().replace(" ", "_")
                        if (robotProfile.profiles[sanitized] != null)
                        {
                            Button.playFail(player)
                            player.sendMessage("${CC.RED}You already have a profile with that name!")
                            PlayerDescribeProfilesMenu(robotProfile, action).openMenu(player)
                            return@acceptInput
                        }

                        Button.playSuccess(player)
                        player.sendMessage("${CC.GREEN}Your profile has been created!")

                        robotProfile.profiles[sanitized] = SettingProfile(sanitized)
                        robotProfile.save()

                        PlayerDescribeProfilesMenu(robotProfile, action).openMenu(player)
                    }
                    .start(player)
            }
    )

    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()
        for (settings in robotProfile.profiles.values)
        {
            buttons[buttons.size] = ItemBuilder
                .of(Material.PAPER)
                .name("${CC.B_GREEN}${settings.id}")
                .addToLore(
                    *settings.describe().toTypedArray(),
                    "",
                    "${CC.GREEN}Click to select!",
                    "${CC.RED}Shift-Click to delete!"
                )
                .glow()
                .toButton { _, type ->
                    if (
                        type == ClickType.SHIFT_RIGHT ||
                        type == ClickType.SHIFT_LEFT
                    )
                    {
                        Button.playSuccess(player)
                        robotProfile.profiles.remove(settings.id)
                        robotProfile.save()

                        openMenu(player)
                        return@toButton
                    }

                    Button.playNeutral(player)
                    action(settings)
                }
        }
        return buttons
    }

    override fun getPrePaginatedTitle(player: Player) = "Viewing profiles..."
}
