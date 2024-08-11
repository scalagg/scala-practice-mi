package land.esta.plugin.ssb.features.imple;

import land.esta.plugin.ssb.SolaraRobotsPlugin;
import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.features.Feature;
import land.esta.plugin.ssb.npc.nms.FixedEntityFishingHook;
import me.lucko.helper.Schedulers;
import me.lucko.helper.promise.Promise;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityEnderPearl;
import net.minecraft.server.v1_8_R3.EntityHuman;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

public class Pearling extends Feature {

    @Override
    public void tickEvent(Bot bot) {
        bot.setPearlTick(bot.getPearlTick() + 1);
        List<ItemStack> pearls = getPearls(bot);

        if (pearls.isEmpty()) return;
        if (bot.getFleeManager().getTicksSinceLastFlee() < 10) return;
        if (bot.getHealthManager().getHealth() < bot.getMovements().getTargetLocate().getTargetEntity().getHealth())
            return;
        if (getAngle(bot) < bot.getProfile().value(RootAccessor.INSTANCE.getPearlSettings().getAngleDifference()))
            return;
        if (bot.getPearlTick() < bot.getProfile().value(RootAccessor.INSTANCE.getPearlSettings().getPearlDelay()))
            return;

        bot.setPearlTick(0);
        pearl(pearls.get(0), bot);
    }

    public void testPearl(Bot bot) {
        pearl(getPearls(bot).get(0), bot);
    }

    public void pearl(ItemStack pearl, Bot bot) {
        bot.getInventoryManager().setMainHand(itemSlot(pearl, bot));

        Promise.start()
                .thenRunDelayedSync(() -> {
                    if (!bot.isAlive()) return;

                    final Location location = bot.getMovements().getLocation();
                    bot.getMovements().rotate(
                            location.getYaw() + bot.getProfile().value(RootAccessor.INSTANCE.getPearlSettings().getPearlThrowYawOffSet()),
                            3.0F
                    );

                    bot.getMovements().getRotations().setRotationLockUntil(System.currentTimeMillis() + 1000L);

/*                    if (getAngle(bot) < bot.getProfile().value(RootAccessor.INSTANCE.getPearlSettings().getAngleDifference()))
                        return;*/

                    bot.getAttackingManager().swing();
                    bot.getInventoryManager().removeItem(itemSlot(pearl, bot));

                    launchProjectile(
                            bot.getNpc().getBukkitEntity().getHandle(),
                            bot.getMovements().getLocation().getDirection()
                    );

                }, bot.getProfile().value(RootAccessor.INSTANCE.getPearlSettings().getPearlingTicks()))
                .thenRunDelayedSync(() -> {
                    bot.getInventoryManager().setMainHand(1);
                }, 10L)
                .bindWith(bot.getTerminable());
    }

    public EntityEnderPearl launchProjectile(EntityHuman human, Vector targetLocation) {
        EntityEnderPearl launch = new EntityEnderPearl(human.getWorld(), human);

        Validate.notNull(launch, "Projectile not supported");

        ((Projectile) launch.getBukkitEntity()).setVelocity(targetLocation);

        human.getWorld().addEntity(launch);
        return launch;
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
    public void teleport(ProjectileHitEvent event) {
        if (!event.getEntity().getType().equals(EntityType.ENDER_PEARL)) return;
        if (event.getEntity().getShooter() instanceof LivingEntity shooter) {
            Bot bot = SolaraRobotsPlugin.getInstance().getBotManager().getNpcFromId(shooter.getEntityId());
            if (bot == null) return;

            var location = event.getEntity().getLocation().clone();
            location.setY(location.getY() + 0.1);
            location.setYaw(bot.getMovements().getLocation().getYaw());
            location.setPitch(bot.getMovements().getLocation().getPitch());
            updateLocation(bot, location);
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
