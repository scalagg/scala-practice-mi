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
public class StrafeSettings extends Category {

    private Option<Boolean> enabled = new Option<>("Strafe",Material.SUGAR_CANE,this,false);
    private Option<Integer> chance = new Option<>("Chance",Material.GOLDEN_CARROT,this,100);
    private Option<Boolean> strafeInCombo = new Option<>("Strafe In Combo",Material.SUGAR,this,false);
    private Option<Integer> switchSideTicks = new Option<>("Switch Side Ticks",Material.TRAP_DOOR,this,20,1);
    //private SettingObject distancing = new SettingObject("Distancing",Material.DIAMOND_BOOTS,false);
    //private SettingObject distance = new SettingObject("Distance",Material.IRON_BOOTS,0,0.1);

    public StrafeSettings() {
        super(
                "strafesettings",
                Arrays.asList(
                        "Configurable Strafe",
                        "settings for the robot."
                ),
                new ItemStack(Material.SUGAR_CANE),
                GlobalsKt.getRoot(),
                "Strafe Settings",
                "botgui.strafesettings"
        );

        addChildren(enabled, chance, strafeInCombo, switchSideTicks);
    }
}
