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
public class VelocitySettings extends Category {

    private Option<Double> horizontal = new Option<>("Horizontal", Material.ANVIL, this,0.47,0.01, 5);
    private Option<Double> vertical = new Option<>("Vertical", Material.ANVIL, this,0.35,0.01, 5);
    private Option<Double> dependentRange = new Option<>("Dependent Range",Material.BLAZE_ROD, this,3.2,0.1, 10);
    private Option<Double> dependentRangeMultiplier = new Option<>("Dependent Range Multiplier",Material.BLAZE_POWDER, this,0.15,0.01, 5);
    private Option<Double> motionFriction = new Option<>("Motion Friction",Material.GOLD_BLOCK, this,1.0,0.02, 5);
    private Option<Double> sprintMultiplier = new Option<>("Sprint Multiplier",Material.DIAMOND_HOE, this,1.2,0.1, 10);
    private Option<Double> groundMultiplier = new Option<>("Ground Multiplier",Material.DIAMOND_SPADE, this,1.1,0.1, 10);

    public VelocitySettings(Category parent) {
        super(
                "velocitysettings",
                Arrays.asList(
                        "Configurable velocity",
                        "settings for the robot."
                ),
                new ItemStack(Material.SLIME_BALL),
                parent,
                "Velocity Settings",
                "botgui.velocitysettings"
        );

        addChildren(horizontal, vertical, dependentRange, dependentRangeMultiplier, motionFriction, sprintMultiplier, groundMultiplier);
    }
}
