package gg.scala.plugin.robots.bots.settings;

import gg.scala.plugin.robots.bot.setting.setting.Category;
import gg.scala.plugin.robots.bot.setting.setting.Option;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
public class PacketsSettings extends Category {
    private final Option<Boolean> relativeMove = new Option<>("Relative Move", Material.STRING, this, false, -1);

    public PacketsSettings(Category parent) {
        super(
                "packetsettings",
                Arrays.asList(
                        "Configurable packet",
                        "settings for the robot."
                ),
                new ItemStack(Material.STRING),
                parent,
                "Packet Settings",
                "botgui.packetsettings"
        );

        addChildren(relativeMove);
    }
}
