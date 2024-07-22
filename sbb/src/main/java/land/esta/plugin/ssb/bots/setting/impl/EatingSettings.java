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
public class EatingSettings extends Category {

    private Option<Integer> startEatingTimer = new Option<>("Start Eating Timer", Material.GOLDEN_APPLE, this, 40, 2);
    private Option<Double> startEatingDistance = new Option<>("Start Eating Distance", Material.MUSHROOM_SOUP, this, 3.0, 1);
    private Option<Integer> eatingGappleHealth = new Option<>("Eating Gapple Health", Material.GOLDEN_APPLE, this, 15, 1);
    private Option<Double> cancelEatingDistance = new Option<>("Jump Eating Distance", Material.BARRIER, this, 4.5, 0.1);
    private Option<Boolean> preGap = new Option<>("Pre Gappling", Material.GOLDEN_CARROT, this, true);

    public EatingSettings(Category parent) {
        super(
                "gapplesettings",
                Arrays.asList(
                        "Configurable gapple",
                        "settings for the robot."
                ),
                new ItemStack(Material.GOLDEN_APPLE),
                parent,
                "Gapple Settings",
                "botgui.gapplesettings"
        );
        addChildren(preGap, startEatingDistance, startEatingTimer, eatingGappleHealth, cancelEatingDistance);
    }
}
