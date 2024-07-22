package land.esta.plugin.ssb.features.imple;

import land.esta.plugin.ssb.SmartBotsBackend;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.botevents.events.*;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.features.Feature;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class EatingAndDrinking extends Feature {

    private int runningTicks = 40;

    @Override
    public void listen(Event event) {
        Bot bot = event.bot;

        if (event instanceof SpawnEvent) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    runEat(bot);
                }
            }.runTaskLater(SmartBotsBackend.getInstance(),1);
        }

        boolean lowHealth = false;
        if (event instanceof BotHealthChangeEvent e) {
            if (e.getAfter() <= bot.getSettingManager().getEatingSettings().getEatingGappleHealth().getIntValue()) lowHealth = true;
        }

        if (event instanceof FinishEatingEvent || event instanceof EffectEndEvent || event instanceof BotRequiredToEatEvent || lowHealth) {
            boolean able = ableToEat(bot.getSettingManager().getEatingSettings().getStartEatingDistance().getDoubleValue(),bot) || bot.getFleeManager().getTicks() > bot.getSettingManager().getEatingSettings().getStartEatingTimer().getIntValue();
            boolean needsHealth = bot.getHealthManager().getHealth() + bot.getHealthManager().getAbsHearts() < bot.getSettingManager().getEatingSettings().getEatingGappleHealth().getIntValue() && bot.getInventoryManager().hasItem(322).size() > 0;

            if (needsHealth) {
                // Prefer to splash potions
                if (!getPotions(bot).isEmpty()) {
                    SmartBotsBackend.getInstance().getFeatureManager().getHealthPotionThrowing().splash(bot, bot.getFleeManager().getTicks());
                    return;
                }

                if (able) {
                    if (!bot.getInventoryManager().isEating())
                        eatGap(bot);
                } else bot.getInventoryManager().setRequiredToEat(true);
            } else bot.getInventoryManager().setRequiredToEat(false);
        }

        if (event instanceof FinishEatingEvent || event instanceof EffectEndEvent || event instanceof BotRequiredToEatEvent) {
            boolean able = ableToEat(bot.getSettingManager().getEatingSettings().getStartEatingDistance().getDoubleValue(),bot) || bot.getFleeManager().getTicks() > bot.getSettingManager().getEatingSettings().getStartEatingTimer().getIntValue();

            if (needsToDrink(bot)) {
                if (able) {
                    if (!bot.getInventoryManager().isEating())
                        runEat(bot);

                } else bot.getInventoryManager().setRequiredToEat(true);
            }
        }

        if (event instanceof EatingCancelEvent) {
            bot.getInventoryManager().setRequiredToEat(true);
        }
    }

    public static boolean needsToDrink(@NotNull Bot bot) {
        List<ItemStack> list = bot.getInventoryManager().hasItem(373);
        AtomicBoolean result = new AtomicBoolean(false);

        list.forEach(item -> {
            Collection<PotionEffect> effects = Potion.fromItemStack(item).getEffects();

            if (Potion.fromItemStack(item).isSplash()) return;
            if (bot.getEffectsManager().hasEffects(effects)) return;
            result.set(true);
        });

        return result.get();
    }

    public static void runEat(@NotNull Bot bot) {
        bot.getInventoryManager().allowEating();
        List<ItemStack> list = bot.getInventoryManager().hasItem(373);

        list.forEach(itemStack -> bot.getInventoryManager().eat(itemStack));
    }

    public static void eatGap(@NotNull Bot bot) {
        if (System.currentTimeMillis() - bot.getInventoryManager().getGappleDelay() < 2000) return; //2 Seconds delay
        bot.getInventoryManager().allowEating();
        List<ItemStack> list = bot.getInventoryManager().hasItem(Material.GOLDEN_APPLE);

        list.forEach(itemStack -> bot.getInventoryManager().eat(itemStack));
        bot.getInventoryManager().setGappleDelay(System.currentTimeMillis());
    }

    @Override
    public void tickEvent(Bot bot) {
        boolean able = ableToEat(bot.getSettingManager().getEatingSettings().getCancelEatingDistance().getDoubleValue(),bot);
        if (!able && bot.getInventoryManager().isEating()) bot.getMovements().setJump(true);
    }

    public boolean ableToEat(double dist, @NotNull Bot bot) {
        if (!bot.getMovements().getTargetLocate().getTargetEntity().getWorld().equals(bot.getMovements().getLocation().getWorld())) return true;
        return bot.getMovements().getTargetLocate().getTargetEntity().getLocation().distance(bot.getMovements().getLocation()) > dist;
    }

    // Method from HealthPotionThrowing class
    public List<ItemStack> getPotions(@NotNull Bot bot) {
        List<ItemStack> potions = bot.getInventoryManager().hasItem(373);
        List<ItemStack> result = new ArrayList<>();

        for (ItemStack item : potions) {
            if (Potion.fromItemStack(item).isSplash() && Potion.fromItemStack(item).getType().equals(PotionType.INSTANT_HEAL)) result.add(item);
        }

        return result;
    }
}
