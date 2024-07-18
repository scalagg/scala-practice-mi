package land.esta.plugin.ssb.commands;

import gg.scala.commons.acf.annotation.CommandAlias;
import gg.scala.commons.acf.annotation.CommandPermission;
import gg.scala.commons.annotations.commands.AutoRegister;
import gg.scala.commons.command.ScalaCommand;
import gg.scala.commons.issuer.ScalaPlayer;
import land.esta.plugin.ssb.SmartBotsBackend;

/**
 * @author GrowlyX
 * @since 7/18/2024
 */
@AutoRegister
public class DifficultiesCommand extends ScalaCommand {

    @CommandAlias("difficulties")
    @CommandPermission("bot.staff")
    public void onBotGUI(ScalaPlayer player) {
        SmartBotsBackend.getInstance().getDifficultiesSettings()
                .getMainGui().minOpen(player.bukkit());
    }
}
