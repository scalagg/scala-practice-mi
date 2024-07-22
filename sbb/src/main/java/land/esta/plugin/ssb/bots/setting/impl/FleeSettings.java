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
public class FleeSettings extends Category {

    private Option<Double> calculatedDistance = new Option<>("Calculated Distance", Material.WATCH, this, 10.0, 1);
    private Option<Boolean> runFromCombos = new Option<>("Run From Combos", Material.DIAMOND_BOOTS, this, false);
    private Option<Integer> runFromCombosHits = new Option<>("Run From Combo Hits", Material.DIAMOND_SWORD, this,3,1);
    private Option<Integer> runFromCombosTime = new Option<>("Run From Combo Ticks", Material.WATCH, this,20,1);
    private Option<Integer> runningRotationSpeed = new Option<>("Run Rotation Speed",Material.WEB, this,40,1);

    public FleeSettings() {
        super(
            "fleesettings",
                Arrays.asList(
                        "Configurable Flee",
                        "settings for the robot."
                ),
                new ItemStack(Material.FEATHER),
                GlobalsKt.getRoot(),
                "Flee Settings",
                "botgui.fleesettings"
        );
        addChildren(calculatedDistance, runFromCombos, runFromCombosHits, runFromCombosTime, runningRotationSpeed);
    }
}
