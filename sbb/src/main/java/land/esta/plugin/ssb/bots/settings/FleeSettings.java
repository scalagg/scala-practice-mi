package land.esta.plugin.ssb.bots.settings;

import land.esta.plugin.ssb.bot.setting.setting.Category;
import land.esta.plugin.ssb.bot.setting.setting.Option;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@Setter
public class FleeSettings extends Category {

    private Option<Double> calculatedDistance = new Option<>("Calculated Distance", Material.WATCH, this, 10.0, 1, 40);
    private Option<Boolean> runFromCombos = new Option<>("Run From Combos", Material.DIAMOND_BOOTS, this, false, -1);
    private Option<Integer> runFromCombosHits = new Option<>("Run From Combo Hits", Material.DIAMOND_SWORD, this,3,1, 20);
    private Option<Integer> runFromCombosTime = new Option<>("Run From Combo Ticks", Material.WATCH, this,20,1, 250);
    private Option<Integer> runningRotationSpeed = new Option<>("Run Rotation Speed",Material.WEB, this,40,1, 200);

    public FleeSettings(Category parent) {
        super(
            "fleesettings",
                Arrays.asList(
                        "Configurable flee",
                        "settings for the robot."
                ),
                new ItemStack(Material.FEATHER),
                parent,
                "Flee Settings",
                "botgui.fleesettings"
        );
        addChildren(calculatedDistance, runFromCombos, runFromCombosHits, runFromCombosTime, runningRotationSpeed);
    }
}
