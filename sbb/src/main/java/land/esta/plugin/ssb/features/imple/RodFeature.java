package land.esta.plugin.ssb.features.imple;

import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.botevents.events.BotDeathEvent;
import land.esta.plugin.ssb.botevents.events.FinalMoveEvent;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.features.Feature;
import land.esta.plugin.ssb.EstaRobotsPlugin;
import land.esta.plugin.ssb.npc.nms.FixedEntityFishingHook;
import me.lucko.helper.Schedulers;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RodFeature extends Feature {

    private final Map<UUID, Integer> prevSlot = new HashMap<>();
    private final Map<UUID, Long> lastSwitch = new HashMap<>();

    @EventHandler
    public void deathEvent(BotDeathEvent event) {
        var uniqueId = event.getBot().getUniqueId();
        lastSwitch.remove(uniqueId);
        prevSlot.remove(uniqueId);
    }

    @Override
    public void listen(Event event) {
        Bot bot = event.bot;
        if (event instanceof FinalMoveEvent) {
            FinalMoveEvent movementEvent = (FinalMoveEvent) event;
            try {
                double distance = movementEvent.getTo().distance(movementEvent.bot.getMovements().getTargetLocate().getTarget());
                ItemStack mainHand = movementEvent.bot.getInventoryManager().getItemInMainHand();

                if (!bot.getProfile().value(RootAccessor.INSTANCE.getRodSettings().getEnabled())) return;

                if (distance >= bot.getProfile().value(RootAccessor.INSTANCE.getRodSettings().getMinRodDistance()) && distance <= bot.getProfile().value(RootAccessor.INSTANCE.getRodSettings().getMaxRodDistance()) && !movementEvent.bot.getFleeManager().isActive()) {
                    if (mainHand == null) return;
                    if (mainHand.getType().equals(Material.FISHING_ROD)) return;
                    if (!bot.getProfile().value(RootAccessor.INSTANCE.getRodSettings().getRodWhileInCombo()) && EstaRobotsPlugin.getInstance().getFeatureManager().getStrafeFeature().getCombo().getOrDefault(movementEvent.bot, 0) > 2 && movementEvent.bot.getDamageManager().getDamageTicks() < 10) return;
                    List<ItemStack> rods = movementEvent.bot.getInventoryManager().hasItem(Material.FISHING_ROD);
                    if (rods.isEmpty()) return;

                    //Hold the fishingRod
                    prevSlot.put(movementEvent.bot.getUniqueId(), movementEvent.bot.getInventoryManager().getMainHand());
                    movementEvent.bot.getInventoryManager().setMainHand(movementEvent.bot.getInventoryManager().getItemSlot(rods.get(0)));
                    movementEvent.bot.getNpc().dead = false;
                    lastSwitch.put(movementEvent.bot.getUniqueId(),System.currentTimeMillis());

                    Schedulers
                            .sync()
                            .run(() -> {
                                EntityHuman npc = movementEvent.bot.getNpc();
                                if (npc != null) {
                                    launchProjectile(npc.world, null, npc);

                                    movementEvent.bot.getAttackingManager().setProjectile(true); // Special velocity for the projectile
                                }
                            })
                            .bindWith(bot.getTerminable());
                } else {
                    if (System.currentTimeMillis() - lastSwitch.getOrDefault(movementEvent.bot.getUniqueId(), 0L) < bot.getProfile().value(RootAccessor.INSTANCE.getRodSettings().getRodDuration())) return;
                    if (mainHand == null) return;
                    if (!mainHand.getType().equals(Material.FISHING_ROD)) return;
                    movementEvent.bot.getInventoryManager().setMainHand(prevSlot.getOrDefault(movementEvent.bot.getUniqueId(),0));
                }

            } catch (IllegalArgumentException ignore) {

            }
        }
    }

    public FixedEntityFishingHook launchProjectile(World world, Vector velocity, EntityHuman human) {
        FixedEntityFishingHook launch = new FixedEntityFishingHook(world, human);

        Validate.notNull(launch, "Projectile not supported");
        if (velocity != null) {
            ((Projectile) launch.getBukkitEntity()).setVelocity(velocity);
        }

        world.addEntity(launch);
        return launch;
    }
}
