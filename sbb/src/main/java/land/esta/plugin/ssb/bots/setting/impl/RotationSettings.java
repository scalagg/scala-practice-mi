package land.esta.plugin.ssb.bots.setting.impl;

import land.esta.plugin.ssb.bot.setting.setting.Category;
import land.esta.plugin.ssb.bot.setting.setting.Option;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@Setter
public class RotationSettings extends Category {

    private Option<Integer> speed = new Option<>("Speed", Material.EXP_BOTTLE,this,18,2, 60);
    private Option<Double> jitterRotation = new Option<>("Jitter Rotation", Material.FLINT,this,1.0, 1, 35);
    private Option<Double> speedRandom = new Option<>("Speed Random", Material.FIREWORK,this,1.0, 1, 35);

    public RotationSettings(Category parent) {
        super(
                "rotationsettings",
                Arrays.asList(
                        "Configurable rotation",
                        "settings for the robot."
                ),
                new ItemStack(Material.WEB),
                parent,
                "Rotation Settings",
                "botgui.rotationsettings"
        );

        addChildren(speed, jitterRotation, speedRandom);
    }
}
