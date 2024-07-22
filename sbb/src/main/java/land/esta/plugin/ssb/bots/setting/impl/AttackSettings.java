package land.esta.plugin.ssb.bots.setting.impl;

import land.esta.plugin.ssb.bot.setting.setting.Category;
import land.esta.plugin.ssb.bot.setting.setting.GlobalsKt;
import land.esta.plugin.ssb.bot.setting.setting.Option;
import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@Setter
public class AttackSettings extends Category {

    private Option<Double> reach = new Option<>("Reach", Material.DIAMOND_SWORD, this, 2.7, 0.1);
    private Option<Integer> cps = new Option<>("CPS", Material.DIAMOND_CHESTPLATE, this, 6);
    private Option<Integer> swingRange = new Option<>("Swing Range", Material.IRON_SWORD, this, 6);
    private Option<Double> playerHitBox = new Option<>("Player Hitbox", Material.IRON_CHESTPLATE, this, 0.3, 0.1);
    private Option<Boolean> advancedReduce = new Option<>("Reduce", Material.STICK, this, false);

    public int getDelay(SettingProfile profile) {
        int cps = profile.value(this.cps);
        return cps > 0 ? 1000 / cps : 1000;
    }

    public AttackSettings() {
        super(
                "attacksettings",
                Arrays.asList(
                        "Configurable PvP mechanics",
                        "for the robot."
                ),
                new ItemStack(Material.DIAMOND_SWORD),
                GlobalsKt.getRoot(),
                "Attack Settings",
                "botgui.attacksettings"
        );
        addChildren(reach, cps, swingRange, advancedReduce, playerHitBox);
    }
}
