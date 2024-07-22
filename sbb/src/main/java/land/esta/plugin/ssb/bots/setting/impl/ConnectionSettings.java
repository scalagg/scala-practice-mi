package land.esta.plugin.ssb.bots.setting.impl;

import land.esta.plugin.ssb.bot.setting.setting.Category;
import land.esta.plugin.ssb.bot.setting.setting.GlobalsKt;
import land.esta.plugin.ssb.bot.setting.setting.Option;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@Setter
public class ConnectionSettings extends Category {

    public Option<Integer> ping = new Option<>("Ping", Material.STRING, this, 100, 5);
    public Option<Boolean> delayedBotLocation = new Option<>("Delayed Bot Location", Material.WATCH, this, true);
    public Option<Integer> connectionPacketDelay = new Option<>("Rel-Move Simulation Ticks", Material.SPIDER_EYE, this, 3, 1);

    public ConnectionSettings(Category parent) {
        super(
                "connectionsettings",
                Arrays.asList(
                        "Configurable connection",
                        "settings for the robot."
                ),
                new ItemStack(Material.DIODE),
                parent,
                "Connection Settings",
                "botgui.connectionsettings"
        );
        addChildren(ping, delayedBotLocation, connectionPacketDelay);
    }
}
