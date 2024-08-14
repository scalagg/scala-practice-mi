package land.esta.plugin.ssb.bots.settings;

import land.esta.plugin.ssb.bot.setting.setting.Category;
import land.esta.plugin.ssb.bot.setting.setting.Option;
import lombok.Getter;
import net.evilblock.cubed.util.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
public class BuildingSettings extends Category {
    private final Option<Boolean> enabled = new Option<>("Enabled", Material.STONE_BUTTON, this, true);
    private final Option<Double> minDistanceFromPlayer = new Option<>("Min Distance From Player", Material.PRISMARINE_SHARD, this, 2.5, 0.5, 10);
    private final Option<Integer> placeCooldown = new Option<>("Place Cooldown", Material.CAULDRON_ITEM, this, 5000, 250, 100000)
            .addDescription(CC.GRAY + "(1000 = 1 Second)");
    private final Option<Integer> buildingTicks = new Option<>("Building Ticks", Material.GHAST_TEAR, this, 8, 2, 120);
    private final Option<Boolean> buildWhenCombo = new Option<>("Build When Comboed", Material.SADDLE, this, false);
    private final Option<Integer> buildWhenComboHits = new Option<>("Build When Comboed Hits", Material.IRON_BARDING, this, 3, 1, 100);
    private final Option<Boolean> buildWhenPlayerIsHigh = new Option<>("Build When Player Is High", Material.CLAY_BALL, this, true);
    private final Option<Integer> buildWhenPlayerIsHighBlocks = new Option<>("Build When Player Is High Blocks", Material.CLAY_BRICK, this, 3, 1, 20);
    private final Option<Integer> buildingUpTicks = new Option<>("Building Up Ticks", Material.GHAST_TEAR, this, 20, 5, 240);

    public BuildingSettings(Category parent) {
        super(
                "buildingsettings",
                Arrays.asList(
                        "Configurable building",
                        "settings for the robot."
                ),
                new ItemStack(Material.WOOL, 1, (short) 11),
                parent,
                "Building Settings",
                "botgui.buildingsettings"
        );

        addChildren(enabled, minDistanceFromPlayer, placeCooldown, buildingTicks, buildWhenCombo, buildWhenComboHits, buildWhenPlayerIsHigh, buildWhenPlayerIsHighBlocks, buildingUpTicks);
    }
}
