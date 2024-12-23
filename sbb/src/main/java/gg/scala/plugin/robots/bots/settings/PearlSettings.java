package gg.scala.plugin.robots.bots.settings;

import gg.scala.plugin.robots.bot.setting.setting.Category;
import gg.scala.plugin.robots.bot.setting.setting.Option;
import gg.scala.plugin.robots.scala.freebotfridays.FreeBotFridays;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
public class PearlSettings extends Category {

    //TODO add pearl chance.
    private final Option<Integer> pearlDelay = new Option<>("Pearl Delay", Material.ENDER_PEARL, this, 600, 5, 2000);
    private final Option<Integer> angleDifference = new Option<>("Angle Difference", Material.NETHER_STAR, this, 130, 5, 400);
    private final Option<Integer> pearlThrowYawOffSet = new Option<>("Pearl Throw YawOffSet", Material.NAME_TAG, this, 10, 1, 40);
    private final Option<Integer> pearlingTicks = new Option<>("Pearling Ticks", Material.WATCH, this, 10, 1, 60);

    @Override
    public boolean hasPermission(@NotNull Player player) {
        return super.hasPermission(player) || FreeBotFridays.INSTANCE.isFreeBotFriday();
    }

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
