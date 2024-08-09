package land.esta.plugin.ssb.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.lemon.LemonConstants
import land.esta.plugin.ssb.bot.menu.profiles.PlayerDescribeProfilesMenu
import land.esta.plugin.ssb.bot.menu.profiles.PlayerProfileBackbone
import land.esta.plugin.ssb.bot.menu.profiles.ProfileCategoryMenu
import land.esta.plugin.ssb.bot.profile.RobotProfileService
import land.esta.plugin.ssb.bot.setting.setting.root
import land.esta.plugin.ssb.solara.freebotfridays.FreeBotFridays
import net.evilblock.cubed.util.CC

/**
 * @author GrowlyX
 * @since 7/18/2024
 */
@AutoRegister
object BotCustomizerCommand : ScalaCommand()
{
    @CommandAlias("customizebots|botgui|custombots|botprofiles|botcustomizer")
    fun onBotCustomizer(player: ScalaPlayer)
    {
        if (player.bukkit().hasPermission("botgui.use") || FreeBotFridays.isFreeBotFriday())
        {
            val profile = RobotProfileService.find(player.bukkit())
                ?: return

            PlayerDescribeProfilesMenu(profile) { settingProfile ->
                ProfileCategoryMenu(
                    settingProfile,
                    PlayerProfileBackbone(player.bukkit()),
                    root
                ).openMenu(player.bukkit())
            }.openMenu(player.bukkit())
        } else
        {
            player.sendMessage(
                CC.RED + "You need the " + CC.D_AQUA + "VIP" + CC.RED + "rank to use this.",
                CC.RED + "Store: " + CC.YELLOW + LemonConstants.STORE_LINK
            )
        }
    }
}
