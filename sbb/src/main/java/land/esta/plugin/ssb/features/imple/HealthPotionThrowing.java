package land.esta.plugin.ssb.features.imple;

import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.botevents.events.BotHealthChangeEvent;
import land.esta.plugin.ssb.botevents.events.RunningEvent;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.features.Feature;
import me.lucko.helper.Schedulers;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class HealthPotionThrowing extends Feature {

    @Override
    public void listen(Event event) {
        if (event instanceof BotHealthChangeEvent e) {
            Bot bot = e.getBot();

            //Health & agro checks
            if (e.getAfter() > 4 || bot.getHealthManager().getHealth() > bot.getMovements().getTargetLocate().getTargetEntity().getHealth())
                return;

            //Getting the potions
            List<ItemStack> potions = getPotions(bot);
            if (potions.isEmpty()) return;

            //Set running
            bot.getFleeManager().setRunning(true);

            //Set holding
            bot.getInventoryManager().setMainHand(bot.getInventoryManager().getItemSlot(potions.get(0)));
        }

        if (event instanceof RunningEvent runningEvent) {
            splash(runningEvent.getBot(), runningEvent.getTicks(), () -> {
                runningEvent.setForceEnd(true);
                runningEvent.getBot().getFleeManager().setRunning(false);
            });
        }
    }

    public void splash(Bot bot, int ticks, Runnable returnToPlayer) {
        //Throwing
        var firstPotionTime = ticks == bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getFirstPotionTick());
        var secondPotionTime = (ticks == bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getSecondPotionTick()) &&
                bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getSecondPotion()));

        //Cancel
        if (
                bot.getHealthManager().getHealth() > bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getFleeHealth()) && nearTarget(bot) ||
                        ticks > bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getFinishPotionTick())
        ) {
            bot.getInventoryManager().setMainHand(1);
            returnToPlayer.run();
            return;
        }

        if (firstPotionTime || secondPotionTime) {
            List<ItemStack> potions = getPotions(bot);
            bot.getInventoryManager().removeItem(
                    bot.getInventoryManager()
                            .getItemSlot(potions.get(0))
            );
            throwPotion(potions.get(0), bot);

            // Set holding
            if (potions.size() > 1) {
                bot.getInventoryManager().setMainHand(
                        bot.getInventoryManager().getItemSlot(potions.get(1))
                );
            } else {
                // We don't have any more potions, so we'll just cancel the flee
                bot.getInventoryManager().setMainHand(1);
                returnToPlayer.run();
            }
        }
    }

    // Gets all the splash potions of a specific type
    public List<ItemStack> getPotions(Bot bot) {
        List<ItemStack> potions = bot.getInventoryManager().hasItem(373);
        List<ItemStack> result = new ArrayList<>();

        for (ItemStack item : potions) {
            if (Potion.fromItemStack(item).isSplash() && Potion.fromItemStack(item).getType().equals(PotionType.INSTANT_HEAL))
                result.add(item);
        }

        return result;
    }

    //Checks if a player is close by
    public boolean nearTarget(Bot bot) {
        Location target = bot.getLagCompensatorManager().getClientLocation();
        if (target == null) {
            return false;
        }
        Location loc = bot.getMovements().getLocation();
        double distance = target.distance(loc);

        return distance < bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getCancelPotionDistance());
    }

    //Throws potions
    public void throwPotion(ItemStack potion, Bot bot) {
        // Spawn the potion
        launchProjectile(potion, bot.getNpc().getBukkitEntity());
    }

    /**
     * Responsible for scaling the thrown potion velocity to match the current
     * speed of the entity, so that the potion doesn't go flying forward and more
     * so splashes near the entity, even if the entity is running.
     */
    public static final double CATCH_UP_SCALING = 0.25;

    //Modified version of BukkitEntity.launchProjectile(Class<? extends T> projectile, Vector velocity).
    public void launchProjectile(ItemStack item, CraftPlayer entity) {
        Schedulers
                .sync()
                .run(() -> {
                    World world = ((CraftWorld) entity.getWorld()).getHandle();
                    net.minecraft.server.v1_8_R3.Entity launch;
                    launch = new EntityPotion(world, entity.getHandle(), CraftItemStack.asNMSCopy(item));

                    Validate.notNull(launch, "Projectile not supported");

                    var initialThrowVelocity = entity.getLocation().getDirection();
                    var playerMotion = entity.getVelocity();
                    var combinedVelocity = initialThrowVelocity.add(playerMotion);
                    var entityCatchUpScaledVelocity = combinedVelocity.multiply(CATCH_UP_SCALING);

                    ((Projectile) launch.getBukkitEntity()).setVelocity(entityCatchUpScaledVelocity);

                    world.addEntity(launch);
                });
    }
}
