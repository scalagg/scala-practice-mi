package land.esta.plugin.ssb.commands;

import gg.scala.commons.acf.InvalidCommandArgument;
import gg.scala.commons.acf.annotation.CommandAlias;
import gg.scala.commons.annotations.commands.AutoRegister;
import gg.scala.commons.command.ScalaCommand;
import gg.scala.commons.issuer.ScalaPlayer;
import land.esta.plugin.ssb.SmartBotsBackend;
import land.esta.plugin.ssb.data.DataPlayer;
import land.esta.plugin.ssb.utils.Color;
import land.esta.plugin.ssb.utils.gui.imple.BotGui;

/**
 * @author GrowlyX
 * @since 7/18/2024
 */
@AutoRegister
public class BotGUICommand extends ScalaCommand {

    @CommandAlias("customizebots")
    public void onBotGUI(ScalaPlayer player) {
        if (player.bukkit().hasPermission("botgui.use")) {
            DataPlayer data = SmartBotsBackend.getInstance()
                    .getDataManager()
                    .getPlayer(player.bukkit());

            BotGui gui = data.getGui();
            if (gui.isLoaded()) {
                gui.getMainGui().openGui();
            } else {
                throw new InvalidCommandArgument("The menu has not loaded, please try again later.");
            }
        } else {
            player.sendMessage(Color.code("&cYou need the &3VIP &crank to use this."));
            player.sendMessage(Color.code("&cStore: &ehttps://store.esta.land"));
        }
    }
}
