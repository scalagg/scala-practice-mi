package land.esta.plugin.ssb.bots.setting.impl;

import land.esta.plugin.ssb.bot.setting.setting.Category;
import land.esta.plugin.ssb.bot.setting.setting.Option;
import lombok.Getter;
import lombok.Setter;
import net.evilblock.cubed.util.CC;
import net.evilblock.cubed.util.bukkit.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@Setter
public class PotionSettings extends Category {

    //Health potion
    private Option<Integer> fleeHealth = new Option<>("Flee Health", Material.APPLE, this, 4, 1, 20);
    private Option<Boolean> agroPotion = new Option<>("Agro Potion", Material.DIAMOND_SWORD, this, false, -1);
    private Option<Integer> firstPotionTick = new Option<>("First Potion Tick", Material.WATCH, this, 9, 1, 60);
    private Option<Integer> secondPotionTick = new Option<>("Second Potion Tick", Material.WATCH, this, 12, 1, 60);
    private Option<Integer> finishPotionTick = new Option<>("Finish Potion Tick", Material.WATCH, this, 22, 1, 60);
    private Option<Integer> tickOffset = new Option<>("Tick Offset", Material.DIODE, this, 1, 1, 20);
    private Option<Boolean> secondPotion = new Option<>("Second Potion", Material.POTION, this, true, -1);
    private Option<Double> cancelPotionDistance = new Option<>("Cancel Potion Distance", Material.HOPPER, this, 3.0, 0.1, 100);

    private Option<Integer> hotbarRefillTime = new Option<>(
            "Hotbar Refill Time",
            ItemBuilder
                    .of(Material.POTION)
                    .data((short) 16421)
                    .build(),
            this,
            2, 1, 4
    ).addDescription(
            CC.GRAY + "The amount of time, in",
            CC.GRAY + "seconds, it takes for the bot",
            CC.GRAY + "to refill its hotbar with health",
            CC.GRAY + "potions when empty."
    );

    public PotionSettings(Category parent) {
        super(
                "potionsettings",
                Arrays.asList(
                        "Configurable potion",
                        "settings for the robot."
                ),
                new ItemStack(Material.POTION),
                parent,
                "Potion Settings",
                "botgui.potionsettings"
        );

        addChildren(fleeHealth, agroPotion, firstPotionTick, secondPotion, finishPotionTick, tickOffset, secondPotion, cancelPotionDistance, hotbarRefillTime);
    }
}
