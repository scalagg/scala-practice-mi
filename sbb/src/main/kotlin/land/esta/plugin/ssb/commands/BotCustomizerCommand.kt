package land.esta.plugin.ssb.commands;

import gg.scala.commons.acf.annotation.CommandAlias;
import gg.scala.commons.annotations.commands.AutoRegister;
import gg.scala.commons.command.ScalaCommand;
import gg.scala.commons.issuer.ScalaPlayer;
import gg.scala.lemon.LemonConstants;
import land.esta.plugin.ssb.bot.menu.profiles.PlayerDescribeProfilesMenu;
import land.esta.plugin.ssb.bot.menu.profiles.PlayerProfileBackbone;
import land.esta.plugin.ssb.bot.menu.profiles.ProfileCategoryMenu;
import land.esta.plugin.ssb.bot.profile.RobotProfileService;
import land.esta.plugin.ssb.bot.setting.setting.GlobalsKt;
import land.esta.plugin.ssb.utils.Color;
import net.evilblock.cubed.util.CC;

/**
 * @author GrowlyX
 * @since 7/18/2024
 */
@AutoRegister
public class BotGUICommand extends ScalaCommand {

    @CommandAlias("customizebots")
    public void onBotGUI(ScalaPlayer player) {
        if (player.bukkit().hasPermission("botgui.use")) {
            final var profile = RobotProfileService.INSTANCE.find(player.bukkit());
            if (profile == null) {
                return;
            }

            new PlayerDescribeProfilesMenu(profile, settingProfile -> {
                new ProfileCategoryMenu(
                        settingProfile,
                        new PlayerProfileBackbone(player.bukkit()),
                        GlobalsKt.getRoot()
                ).openMenu(player.bukkit());
                return null;
            }).openMenu(player.bukkit());
        } else {
            player.sendMessage(
                    CC.RED + "You need the " + CC.D_AQUA + "VIP" + CC.RED + "rank to use this.",
                    CC.RED + "Store: " + CC.YELLOW + LemonConstants.getSTORE_LINK()
            );
        }
    }
}
