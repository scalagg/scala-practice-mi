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
    public class PotionSetting extends Category {

    //Health potion
    private Option<Integer> fleeHealth = new Option<>("Flee Health", Material.APPLE,this,4,1);
    private Option<Boolean> agroPotion = new Option<>("Agro Potion",Material.DIAMOND_SWORD,this,false);
    private Option<Integer> firstPotionTick = new Option<>("First Potion Tick", Material.WATCH,this,9,1);
    private Option<Integer> secondPotionTick = new Option<>("Second Potion Tick", Material.WATCH,this,12,1);
    private Option<Integer> finishPotionTick = new Option<>("Finish Potion Tick", Material.WATCH,this,22,1);
    private Option<Integer> tickOffset = new Option<>("Tick Offset", Material.DIODE,this,1,1);
    private Option<Boolean> secondPotion = new Option<>("Second Potion",Material.POTION,this, true);
    private Option<Double> cancelPotionDistance = new Option<>("Cancel Potion Distance",Material.HOPPER,this,3.0,0.1);

    public PotionSetting() {
        super(
                "potionsettings",
                Arrays.asList(
                        "Configurable Potion",
                        "settings for the robot."
                ),
                new ItemStack(Material.POTION),
                GlobalsKt.getRoot(),
                "Potion Settings",
                "botgui.potionsettings"
        );

        addChildren(fleeHealth, agroPotion, firstPotionTick, secondPotion, finishPotionTick, tickOffset, secondPotion, cancelPotionDistance);
    }
}
