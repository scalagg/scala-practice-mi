package land.esta.plugin.ssb.features.imple;

import land.esta.plugin.ssb.EstaRobotsPlugin;
import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import land.esta.plugin.ssb.botevents.events.BotDeathEvent;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.features.Feature;
import me.lucko.helper.Schedulers;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Pearling extends Feature {

    private final Map<UUID, Integer> pearlTick = new HashMap<>();

    @EventHandler
    public void botDeath(BotDeathEvent event) {
        pearlTick.remove(event.getBot().getUniqueId());
    }

    @Override
    public void tickEvent(Bot bot) {
        pearlTick.put(bot.getUniqueId(), pearlTick.getOrDefault(bot.getUniqueId(), 0) + 1);
        List<ItemStack> pearls = getPearls(bot);

        if (pearls.isEmpty()) return;
        if (bot.getFleeManager().getOffTicks() < 10) return;
        if (bot.getHealthManager().getHealth() < bot.getMovements().getTargetLocate().getTargetEntity().getHealth())
            return;
        if (getAngle(bot) < bot.getProfile().value(RootAccessor.INSTANCE.getPearlSettings().getAngleDifference()))
            return;
        if (pearlTick.get(bot.getUniqueId()) < bot.getProfile().value(RootAccessor.INSTANCE.getPearlSettings().getPearlDelay()))
            return;

        pearl(pearls.get(0), bot);
    }

    public void pearl(ItemStack pearl, Bot bot) {
        this.pearlTick.put(bot.getUniqueId(), 0);
        bot.getInventoryManager().setMainHand(itemSlot(pearl, bot));

        Schedulers
                .sync()
                .runLater(() -> {
                    if (!bot.isAlive()) return;
                    Location location = bot.getMovements().getLocation();
                    location.setYaw(location.getYaw() + bot.getProfile().value(RootAccessor.INSTANCE.getPearlSettings().getPearlThrowYawOffSet()));
                    location.setPitch(3);
                    updateLocation(bot, location);
                    bot.getInventoryManager().setMainHand(1);

                    if (getAngle(bot) < bot.getProfile().value(RootAccessor.INSTANCE.getPearlSettings().getAngleDifference()))
                        return;
                    bot.getInventoryManager().removeItem(itemSlot(pearl, bot));
                    bot.getNpc().getBukkitEntity().launchProjectile(EnderPearl.class);
                }, bot.getProfile().value(RootAccessor.INSTANCE.getPearlSettings().getPearlingTicks()))
                .bindWith(bot.getTerminable());
    }

    public void updateLocation(Bot bot, Location location) {
        bot.getMovements().setLocation(location);
        final double x = location.getX();
        final double y = location.getY();
        final double z = location.getZ();
        final float yaw = location.getYaw();
        final float pitch = location.getPitch();
        bot.getNpc().setLocation(x, y, z, yaw, pitch);
    }

    @EventHandler
    public void teleport(ProjectileLaunchEvent event) {
        if (!event.getEntity().getType().equals(EntityType.ENDER_PEARL)) return;
        if (event.getEntity().getShooter() instanceof LivingEntity shooter) {
            Bot bot = EstaRobotsPlugin.getInstance().getBotManager().getNpcFromId(shooter.getEntityId());
            if (bot == null) return;
            final Location[] trackedLoc = {event.getEntity().getLocation()};

            Schedulers
                    .sync()
                    .runRepeating(task -> {
                        if (event.getEntity().isDead()) {
                            updateLocation(bot, trackedLoc[0]);
                            task.closeAndReportException();
                        } else {
                            trackedLoc[0] = event.getEntity().getLocation();
                        }
                    }, 1L, 1L)
                    .bindWith(bot.getTerminable());
        }
    }

    public int itemSlot(ItemStack stack, Bot bot) {
        return bot.getInventoryManager().getItemSlot(stack);
    }

    public float getAngle(Bot bot) {
        Location from = bot.getMovements().getLocation();
        Location to = bot.getMovements().getTargetLocate().getTargetEntity().getLocation();

        float yaw = (float) calculateYawDifference(to, from).getX();
        yaw -= to.getYaw();
        if (yaw > 180) yaw -= 360;
        return Math.abs(yaw);
    }

    public static Vector calculateYawDifference(Location from, Location to) {
        Location clonedFrom = from.clone();
        Vector startVector = clonedFrom.toVector();
        Vector targetVector = to.toVector();
        clonedFrom.setDirection(targetVector.subtract(startVector));

        return new Vector(clonedFrom.getYaw(), 0, clonedFrom.getPitch());
    }

    public List<ItemStack> getPearls(Bot bot) {
        return bot.getInventoryManager().hasItem(Material.ENDER_PEARL);
    }
}
