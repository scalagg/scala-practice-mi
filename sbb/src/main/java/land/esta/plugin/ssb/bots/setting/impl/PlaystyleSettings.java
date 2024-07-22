package land.esta.plugin.ssb.bots.setting.impl;

import land.esta.plugin.ssb.bot.setting.setting.Category;
import land.esta.plugin.ssb.bot.setting.setting.GlobalsKt;
import land.esta.plugin.ssb.bot.setting.setting.Option;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
public class PlaystyleSettings extends Category {
    private final Option<Boolean> hitSelect = new Option<>("Hit Select",Material.STICK,this,false, -1);
    private final Option<Integer> hitSelectTicks = new Option<>("Hit Select Ticks",Material.STICK,this,5, 1, 60);

    private final Option<Integer> hitSelectDelay = new Option<>("Hit Select Delay",Material.STICK,this,5, 1, 60);

    private final Option<Integer> jumpComboCPS = new Option<>("Jump Combo CPS",Material.STICK,this,4, 1, 25);

    private final Option<Boolean> flips = new Option<>("Flicks (Recommended to have high rotation speed)",Material.SLIME_BALL,this,false, -1);
    private final Option<Integer> flipYaw = new Option<>("Flicks Yaw (360, 180, 90...)",Material.SLIME_BALL,this,35,5, 360);
    private final Option<Integer> flipPitch = new Option<>("Flicks Pitch (25, 15, 0...)",Material.SLIME_BALL,this,5,2, 25);
    private final Option<Integer> flipLong = new Option<>("Flicks Long",Material.SLIME_BALL,this,5, 1, 25);
    private final Option<Integer> flipChance = new Option<>("Flicks Chance",Material.SLIME_BALL,this,75,5, 100);

    private final Option<Boolean> flipSync = new Option<>("Flicks Sync w/ WTaps",Material.SLIME_BALL,this,true, -1);

    private final Option<Boolean> alwaysJump = new Option<>("Jump",Material.SADDLE,this,false, -1);

    private final Option<Boolean> jumpWhileCombo = new Option<>("Jump While Comboed",Material.SADDLE,this,false, -1);

    private final Option<Integer> jumpWhileComboHits = new Option<>("Jump While Comboed Hits",Material.SADDLE,this,4, -1);

    private final Option<Boolean> jumpCombo = new Option<>("Jump Combo",Material.SADDLE,this,false, -1);

    private final Option<Integer> jumpComboHits = new Option<>("Jump Combo Hits",Material.SADDLE,this,4, 1, 30);

    public PlaystyleSettings(Category parent) {
        super(
                "playstylesettings",
                Arrays.asList(
                        "Configurable playstyle",
                        "settings for the robot."
                ),
                new ItemStack(Material.ENDER_PEARL),
                parent,
                "Play Style Settings",
                "botgui.playstyle"
        );

        addChildren(hitSelect, hitSelectDelay, jumpComboCPS, flips, flipYaw, flipPitch, flipLong, flipChance, flipSync, alwaysJump, jumpWhileCombo, jumpWhileComboHits, jumpCombo, jumpComboHits);
    }
}
