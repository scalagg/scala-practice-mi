package land.esta.plugin.ssb.bots.setting.impl;

import land.esta.plugin.ssb.bot.setting.setting.Category;
import land.esta.plugin.ssb.bot.setting.setting.GlobalsKt;
import land.esta.plugin.ssb.bot.setting.setting.Option;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
public class RodSettings extends Category {
    public final Option<Boolean> enabled = new Option<>("Rod Enabled",Material.FISHING_ROD, this,false, -1);
    public final Option<Double> minRodDistance = new Option<>("Minimum Rod Throwing Distance",Material.FISHING_ROD, this,5.0,0.1, 25);
    public final Option<Double> maxRodDistance = new Option<>("Maximum Rod Throwing Distance",Material.FISHING_ROD, this,7.5,0.1, 100);
    public final Option<Integer> rodDuration = new Option<>("Rod Duration (MS)",Material.FISHING_ROD, this,750, 10, 2500);
    public final Option<Boolean> rodWhileInCombo = new Option<>("Throw rod while in combo",Material.FISHING_ROD, this,false, -1);

    public RodSettings(Category parent) {
        super(
                "rodsettings",
                Arrays.asList(
                        "Configurable rod",
                        "settings for the robot."
                ),
                new ItemStack(Material.FISHING_ROD),
                parent,
                "Rod Settings",
                "botgui.rodsettings"
        );

        addChildren(enabled, minRodDistance, maxRodDistance, rodDuration, rodWhileInCombo);
    }
}
