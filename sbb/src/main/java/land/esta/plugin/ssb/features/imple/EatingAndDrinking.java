package land.esta.plugin.ssb.features.imple;

import land.esta.plugin.ssb.SolaraRobotsPlugin;
import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.botevents.events.*;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.features.Feature;
import me.lucko.helper.Schedulers;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class EatingAndDrinking extends Feature {

    private final int runningTicks = 40;

    @Override
    public void listen(Event event) {
        Bot bot = event.getBot();

        if (event instanceof SpawnEvent) {
            Schedulers
                    .sync()
                    .runLater(() -> runEat(bot), 1L)
                    .bindWith(bot.getTerminable());
        }

        int eatingGappleHealth = bot.getProfile().value(
                RootAccessor.INSTANCE.getEatingSettings().getEatingGappleHealth()
        );

        double startEatingDistance = bot.getProfile().value(
                RootAccessor.INSTANCE.getEatingSettings().getStartEatingDistance()
        );

        int startEatingTimer = bot.getProfile().value(
                RootAccessor.INSTANCE.getEatingSettings().getStartEatingTimer()
        );

        boolean lowHealth = false;
        if (event instanceof BotHealthChangeEvent e) {
            if (e.getAfter() <= eatingGappleHealth) lowHealth = true;
        }

        if (event instanceof FinishEatingEvent || event instanceof EffectEndEvent || event instanceof BotRequiredToEatEvent || lowHealth) {
            boolean able = ableToEat(startEatingDistance, bot) || bot.getFleeManager().getTicks() > startEatingTimer;
            boolean needsHealth = bot.getHealthManager().getHealth() + bot.getHealthManager().getAbsHearts() < eatingGappleHealth && !bot.getInventoryManager().hasItem(322).isEmpty();

            if (needsHealth) {
                // Prefer to splash potions
                if (!getPotions(bot).isEmpty()) {
                    SolaraRobotsPlugin.getInstance().getFeatureManager().getHealthPotionThrowing()
                            .splash(
                                    bot,
                                    bot.getFleeManager().getTicks(),
                                    () -> { },
                                    false,
                                    () -> { }
                            );
                    return;
                }

                if (able) {
                    if (!bot.getInventoryManager().isEating())
                        eatGap(bot);
                } else bot.getInventoryManager().setRequiredToEat(true);
            } else bot.getInventoryManager().setRequiredToEat(false);
        }

        if (event instanceof FinishEatingEvent || event instanceof EffectEndEvent || event instanceof BotRequiredToEatEvent) {
            boolean able = ableToEat(startEatingDistance, bot) || bot.getFleeManager().getTicks() > startEatingTimer;

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

    public boolean needsToDrink(Bot bot) {
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

    public void runEat(Bot bot) {
        bot.getInventoryManager().allowEating();
        List<ItemStack> list = bot.getInventoryManager().hasItem(373);

        list.forEach(itemStack -> bot.getInventoryManager().eat(itemStack));
    }

    public static void eatGap(Bot bot) {
        if (System.currentTimeMillis() - bot.getInventoryManager().getGappleDelay() < 2000) return; //2 Seconds delay
        bot.getInventoryManager().allowEating();
        List<ItemStack> list = bot.getInventoryManager().hasItem(322);

        list.forEach(itemStack -> bot.getInventoryManager().eat(itemStack));
        bot.getInventoryManager().setGappleDelay(System.currentTimeMillis());
    }

    @Override
    public void tickEvent(Bot bot) {
        var cancelEatingDistance = bot.getProfile().value(
                RootAccessor.INSTANCE.getEatingSettings().getCancelEatingDistance()
        );

        boolean able = ableToEat(cancelEatingDistance, bot);
        if (!able && bot.getInventoryManager().isEating()) bot.getMovements().setJump(true);
    }

    public boolean ableToEat(double dist, Bot bot) {
        if (!bot.getMovements().getTargetLocate().getTargetEntity().getWorld().equals(bot.getMovements().getLocation().getWorld()))
            return true;
        return bot.getMovements().getTargetLocate().getTargetEntity().getLocation().distance(bot.getMovements().getLocation()) > dist;
    }

    // Method from HealthPotionThrowing class
    public List<ItemStack> getPotions(Bot bot) {
        List<ItemStack> potions = bot.getInventoryManager().hasItem(373);
        List<ItemStack> result = new ArrayList<>();

        for (ItemStack item : potions) {
            if (Potion.fromItemStack(item).isSplash() && Potion.fromItemStack(item).getType().equals(PotionType.INSTANT_HEAL))
                result.add(item);
        }

        return result;
    }
}
