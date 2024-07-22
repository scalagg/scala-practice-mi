package land.esta.plugin.ssb.bots.setting.impl;

import land.esta.plugin.ssb.bot.setting.setting.Category;
import land.esta.plugin.ssb.bot.setting.setting.GlobalsKt;
import land.esta.plugin.ssb.bot.setting.setting.Option;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
public class PearlSettings extends Category {

    //TODO add pearl chance.
    private Option<Integer> pearlDelay = new Option<>("Pearl Delay",Material.ENDER_PEARL,this,600,5,2000);
    private Option<Integer> angleDifference = new Option<>("Angle Difference",Material.NETHER_STAR,this,130,5, 400);
    private Option<Integer> pearlThrowYawOffSet = new Option<>("Pearl Throw YawOffSet",Material.NAME_TAG,this,10,1, 40);
    private Option<Integer> pearlingTicks = new Option<>("Pearling Ticks",Material.WATCH,this,10,1, 60);

    public PearlSettings(Category parent) {
        super(
                "pearlsettings",
                Arrays.asList(
                        "Configurable pearl",
                        "settings for the robot."
                ),
                new ItemStack(Material.ENDER_PEARL),
                parent,
                "Pearl Settings",
                "botgui.pearlsettings"
        );
        addChildren(pearlDelay, angleDifference, pearlThrowYawOffSet, pearlingTicks);
    }
}
