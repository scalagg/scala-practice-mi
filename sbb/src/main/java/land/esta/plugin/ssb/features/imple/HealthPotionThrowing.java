package land.esta.plugin.ssb.features.imple;

import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.botevents.events.BotHealthChangeEvent;
import land.esta.plugin.ssb.botevents.events.RunningEvent;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.features.Feature;
import land.esta.plugin.ssb.SmartBotsBackend;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class HealthPotionThrowing extends Feature {

    @Override
    public void listen(Event event) {
        if (event instanceof BotHealthChangeEvent e) {
            Bot bot = e.bot;

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

        if (event instanceof RunningEvent e) {

            if (splash(e.bot, e.getTicks())) {
                e.setForceEnd(true);
                e.bot.getFleeManager().setRunning(false);
            }
        }
    }

    public boolean splash(Bot bot, int ticks) {
        //Throwing
        if (ticks == bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getFirstPotionTick()) || ticks == bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getSecondPotionTick()) && bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getSecondPotion())) {
            List<ItemStack> potions = getPotions(bot);
            bot.getInventoryManager().removeItem(bot.getInventoryManager().getItemSlot(potions.get(0)));
            throwPotion(potions.get(0), bot);

            //Set holding
            /*if (potions.size() > 1)
                bot.getInventoryManager().setMainHand(bot.getInventoryManager().getItemSlot(potions.get(1)));

            if (potions.size() <= 1) {
                bot.getInventoryManager().setMainHand(1);
            }*/
            bot.getInventoryManager().setMainHand(1);
            return true;
        }

        //Cancel
        if (bot.getHealthManager().getHealth() > bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getFleeHealth()) && nearTarget(bot) || ticks > bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getFinishPotionTick())) {
            bot.getInventoryManager().setMainHand(1);
            return true;
        }

        return false;
    }

    //Gets all the splash potions of a specific type
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
        Location loc = bot.getMovements().getLocation();
        double distance = target.distance(loc);

        return distance < bot.getProfile().value(RootAccessor.INSTANCE.getPotionSettings().getCancelPotionDistance());
    }

    //Throws potions
    public void throwPotion(ItemStack potion, Bot bot) {
        // Spawn the potion
        launchProjectile(potion, null, bot.getNpc().getBukkitEntity());

        //Adds health //TODO replace with a system that listen for potion splash event and if the bot is in the right distance.
        bot.getHealthManager().addHealth(4 * 2);
    }

    //Modified version of BukkitEntity.launchProjectile(Class<? extends T> projectile, Vector velocity).
    public void launchProjectile(ItemStack item, Vector velocity, CraftPlayer entity) {
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = ((CraftWorld) entity.getWorld()).getHandle();
                net.minecraft.server.v1_8_R3.Entity launch;
                launch = new EntityPotion(world, entity.getHandle(), CraftItemStack.asNMSCopy(item));

                Validate.notNull(launch, "Projectile not supported");
                if (velocity != null) {
                    ((Projectile) launch.getBukkitEntity()).setVelocity(velocity);
                }

                world.addEntity(launch);
            }
        }.runTask(SmartBotsBackend.getInstance());
    }
}
