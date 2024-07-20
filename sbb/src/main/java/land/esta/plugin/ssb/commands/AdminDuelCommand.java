package land.esta.plugin.ssb.commands;

import gg.scala.commons.acf.InvalidCommandArgument;
import gg.scala.commons.acf.annotation.CommandAlias;
import gg.scala.commons.acf.annotation.CommandPermission;
import gg.scala.commons.annotations.commands.AutoRegister;
import gg.scala.commons.command.ScalaCommand;
import gg.scala.commons.issuer.ScalaPlayer;
import land.esta.plugin.ssb.SmartBotsBackend;
import land.esta.plugin.ssb.bot.menu.BotDuelMenu;
import land.esta.plugin.ssb.data.DataPlayer;
import land.esta.plugin.ssb.utils.Color;
import land.esta.plugin.ssb.utils.gui.imple.BotGui;

/**
 * Class created on 7/19/2024
 *
 * @author Max C.
 * @project esta-practice
 * @website https://solo.to/redis
 */
@AutoRegister
public class AdminDuelCommand extends ScalaCommand {

    @CommandAlias("adminduel")
    @CommandPermission("bot.staff")
    public void onAdminDUel(ScalaPlayer player) {
        new BotDuelMenu().openMenu(player.bukkit());
    }
}
