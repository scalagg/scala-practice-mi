package land.esta.plugin.ssb.bots.setting.impl;

import land.esta.plugin.ssb.bot.setting.setting.Category;
import land.esta.plugin.ssb.bot.setting.setting.GlobalsKt;
import land.esta.plugin.ssb.bot.setting.setting.Option;
import lombok.Getter;
import net.evilblock.cubed.util.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@Getter
public class PathfindingSettings extends Category {
    private final Option<Boolean> enabled = new Option<>("Enabled", Material.STONE_BUTTON, this, true);
    private final Option<Double> distanceFromLastLocation = new Option<>("Distance From Last Location", Material.STRING, this, 0.1, 0.05, 2);
    private final Option<Integer> getPathCooldown = new Option<>("Get Path Cooldown", Material.BLAZE_POWDER, this, 500, 100, 5000)
            .addDescription(CC.GRAY + "(1000 = 1 Second)");
    private final Option<Double> minDistanceFromPlayer = new Option<>("Min Distance From Player", Material.BOWL, this, 1.0, 0.1, 10);
    private final Option<Integer> cooldownAfterAttack = new Option<>("Cooldown After Attack", Material.GOLD_NUGGET, this, 4000, 250, 15000)
            .addDescription(CC.GRAY + "(1000 = 1 Second)");

    public PathfindingSettings(Category parent) {
        super(
                "pathfindingsettings",
                Arrays.asList(
                        "Configurable pathfinding",
                        "settings for the robot."
                ),
                new ItemStack(Material.DETECTOR_RAIL),
                parent,
                "Pathfinding Settings",
                "botgui.pathfindingsettings"
        );

        addChildren(enabled, distanceFromLastLocation, getPathCooldown, minDistanceFromPlayer, cooldownAfterAttack);
    }
}
