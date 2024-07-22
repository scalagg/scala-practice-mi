package land.esta.plugin.ssb.commands;

import gg.scala.commons.acf.annotation.CommandAlias;
import gg.scala.commons.acf.annotation.CommandPermission;
import gg.scala.commons.annotations.commands.AutoRegister;
import gg.scala.commons.command.ScalaCommand;
import gg.scala.commons.issuer.ScalaPlayer;
import land.esta.plugin.ssb.bot.menu.profiles.GenericDescribeProfilesMenu;
import land.esta.plugin.ssb.bot.menu.profiles.ProfileCategoryMenu;
import land.esta.plugin.ssb.bot.setting.BotSettingService;
import land.esta.plugin.ssb.bot.setting.setting.GlobalsKt;

/**
 * @author GrowlyX
 * @since 7/18/2024
 */
@AutoRegister
public class DifficultiesCommand extends ScalaCommand {

    @CommandAlias("difficulties")
    @CommandPermission("bot.staff")
    public void onBotGUI(ScalaPlayer player) {
        new GenericDescribeProfilesMenu(
                () -> BotSettingService.INSTANCE
                        .cached()
                        .getMap()
                        .values()
                        .stream()
                        .toList(),
                settingProfile -> {
                    new ProfileCategoryMenu(
                            settingProfile,
                            BotSettingService.INSTANCE::sync,
                            GlobalsKt.getRoot()
                    ).openMenu(player.bukkit());
                    return null;
                }
        ).openMenu(player.bukkit());
    }
}
