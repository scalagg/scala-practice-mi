package land.esta.plugin.ssb.bots.setting.impl;

import land.esta.plugin.ssb.bot.setting.setting.Category;
import land.esta.plugin.ssb.bot.setting.setting.GlobalsKt;
import land.esta.plugin.ssb.bot.setting.setting.Option;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
public class PacketsSettings extends Category {
    private Option<Boolean> relativeMove = new Option<>("Relative Move", Material.STRING, this, false);

    public PacketsSettings() {
        super(
                "packetsettings",
                Arrays.asList(
                        "Configurable Packet",
                        "settings for the robot."
                ),
                new ItemStack(Material.STRING),
                GlobalsKt.getRoot(),
                "Packet Settings",
                "botgui.packetsettings"
        );

        addChildren(relativeMove);
    }
}
