package gg.scala.plugin.robots.bots.settings;

import gg.scala.plugin.robots.bot.setting.setting.Category;
import gg.scala.plugin.robots.bot.setting.setting.Option;
import gg.scala.plugin.robots.scala.freebotfridays.FreeBotFridays;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@Setter
public class StrafeSettings extends Category {

    public Option<Boolean> enabled = new Option<>("Strafe", Material.SUGAR_CANE, this, false, -1);
    public Option<Integer> chance = new Option<>("Chance", Material.GOLDEN_CARROT, this, 100, 1, 100);
    public Option<Boolean> strafeInCombo = new Option<>("Strafe In Combo", Material.SUGAR, this, false, -1);
    public Option<Integer> switchSideTicks = new Option<>("Switch Side Ticks", Material.TRAP_DOOR, this, 20, 1, 120);
    //private SettingObject distancing = new SettingObject("Distancing",Material.DIAMOND_BOOTS,false);
    //private SettingObject distance = new SettingObject("Distance",Material.IRON_BOOTS,0,0.1);

    @Override
    public boolean hasPermission(@NotNull Player player) {
        return super.hasPermission(player) || FreeBotFridays.INSTANCE.isFreeBotFriday();
    }

    public StrafeSettings(Category parent) {
        super(
                "strafesettings",
                Arrays.asList(
                        "Configurable strafe",
                        "settings for the robot."
                ),
                new ItemStack(Material.SUGAR_CANE),
                parent,
                "Strafe Settings",
                "botgui.strafesettings"
        );

        addChildren(enabled, chance, strafeInCombo, switchSideTicks);
    }
}
