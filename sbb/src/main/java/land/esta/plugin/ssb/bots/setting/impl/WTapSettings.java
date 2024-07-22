package land.esta.plugin.ssb.bots.setting.impl;

import land.esta.plugin.ssb.bot.setting.setting.Category;
import land.esta.plugin.ssb.bot.setting.setting.GlobalsKt;
import land.esta.plugin.ssb.bot.setting.setting.Option;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
public class WTapSettings extends Category {
    public final Option<Boolean> enabled = new Option<>("W-Tap", Material.DIAMOND_HELMET, this, true);
    public final Option<Integer> chance = new Option<>("Chance", Material.DIAMOND_CHESTPLATE, this, 100, 25);
    public final Option<Integer> tapLong = new Option<>("Tap Long", Material.DIAMOND_LEGGINGS, this, 1, 1);
    public final Option<Integer> comboTapLong = new Option<>("Combo Tap Long", Material.DIAMOND_AXE, this, 1, 1);
    public final Option<Integer> tapStart = new Option<>("Tap Start", Material.DIAMOND_BOOTS, this, 3, 1);
    public final Option<Boolean> blockingHitting = new Option<>("Block Hitting", Material.DIAMOND_SWORD, this, false);

    public WTapSettings(Category parent) {
        super(
                "wtapsettings",
                Arrays.asList(
                        "Configurable WTap",
                        "settings for the robot."
                ),
                new ItemStack(Material.DIAMOND_BOOTS),
                parent,
                    "WTap Settings",
                "botgui.wtapsettings"
        );

        addChildren(enabled, chance, tapLong, comboTapLong, tapStart, blockingHitting);
    }
}
