package land.esta.plugin.ssb.bots.setting.impl;

import land.esta.plugin.ssb.bot.setting.setting.Category;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public class WTapSetting extends Category {

    private final SettingObject enabled = new SettingObject("W-Tap", Material.DIAMOND_HELMET, true);
    private final SettingObject chance = new SettingObject("Chance", Material.DIAMOND_CHESTPLATE, 100, 25);
    private final SettingObject tapLong = new SettingObject("Tap Long", Material.DIAMOND_LEGGINGS, 1, 1);
    private final SettingObject comboTapLong = new SettingObject("Combo Tap Long", Material.DIAMOND_AXE, 1, 1);
    private final SettingObject tapStart = new SettingObject("Tap Start", Material.DIAMOND_BOOTS, 3, 1);
    private final SettingObject blockingHitting = new SettingObject("Block Hitting", Material.DIAMOND_SWORD, false);

    public WTapSetting() {
        super(Material.DIAMOND_BOOTS, "&c&lW-Tap Settings", "WTap", "wtapsettings", 21, true, "&f In this category", "&f you are able to", "&f modify the bot's", "&f w-tap settings.", "", "&aClick to select!");

        addSettings(enabled, chance, comboTapLong, tapLong, tapStart, blockingHitting);
    }
}
