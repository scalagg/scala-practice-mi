package gg.scala.plugin.robots.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Subcommand
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.plugin.robots.bot.menu.management.UnrankedBotManagementMenu
import gg.scala.plugin.robots.bot.menu.profiles.GenericDescribeProfilesMenu
import gg.scala.plugin.robots.bot.menu.profiles.ProfileBackbone
import gg.scala.plugin.robots.bot.menu.profiles.ProfileCategoryMenu
import gg.scala.plugin.robots.bot.setting.BotSettingService
import gg.scala.plugin.robots.bot.setting.setting.persistence.SettingProfile
import gg.scala.plugin.robots.bot.setting.setting.root
import gg.scala.plugin.robots.commands.UnrankedAdminCommand.available
import net.evilblock.cubed.util.CC

/**
 * @author Topu
 * @date 1/18/2025
 */
@AutoRegister
@CommandAlias("sbbadmin|scala-smart-botsadmin")
@CommandPermission("sbb.admin")
object SBBAdminCommands
{
    @Subcommand("difficulties")
    fun onDifficulties(player: ScalaPlayer)
    {
        GenericDescribeProfilesMenu(
            {
                BotSettingService
                    .cached()
                    .map
                    .values
                    .stream()
                    .toList()
            },
            { settingProfile: SettingProfile? ->
                ProfileCategoryMenu(
                    settingProfile!!,
                    { settings: SettingProfile ->
                        val cached = BotSettingService.cached()
                        cached.map[settings.id] = settings
                        BotSettingService.sync(cached)
                    },
                    root
                ).openMenu(player.bukkit())
            }
        ).openMenu(player.bukkit())
    }

    @Subcommand("unranked")
    fun onUnranked(player: ScalaPlayer)
    {
        UnrankedBotManagementMenu().openMenu(player.bukkit())
    }

    @Subcommand("unranked-toggle")
    fun onUnrankedBotToggle(player: ScalaPlayer)
    {
        available = !available
        player.sendMessage("${CC.GREEN}Toggled to: $available")
    }
}