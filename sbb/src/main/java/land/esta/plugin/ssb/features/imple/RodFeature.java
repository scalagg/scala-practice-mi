package land.esta.plugin.ssb.features.imple;

import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.botevents.events.BotDeathEvent;
import land.esta.plugin.ssb.botevents.events.FinalMoveEvent;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.features.Feature;
import land.esta.plugin.ssb.SolaraRobotsPlugin;
import land.esta.plugin.ssb.npc.nms.FixedEntityFishingHook;
import me.lucko.helper.Schedulers;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

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
        Bot bot = event.getBot();
        if (event instanceof FinalMoveEvent) {
            FinalMoveEvent movementEvent = (FinalMoveEvent) event;
            try {
                double distance = movementEvent.getTo().distance(movementEvent.getBot().getMovements().getTargetLocate().getTarget());
                ItemStack mainHand = movementEvent.getBot().getInventoryManager().getItemInMainHand();

                if (!bot.getProfile().value(RootAccessor.INSTANCE.getRodSettings().getEnabled())) return;

                if (distance >= bot.getProfile().value(RootAccessor.INSTANCE.getRodSettings().getMinRodDistance()) && distance <= bot.getProfile().value(RootAccessor.INSTANCE.getRodSettings().getMaxRodDistance()) && !movementEvent.getBot().getFleeManager().isActive()) {
                    if (mainHand == null) return;
                    if (mainHand.getType().equals(Material.FISHING_ROD)) return;
                    if (!bot.getProfile().value(RootAccessor.INSTANCE.getRodSettings().getRodWhileInCombo()) && SolaraRobotsPlugin.getInstance().getFeatureManager().getStrafeFeature().getCombo().getOrDefault(movementEvent.getBot(), 0) > 2 && movementEvent.getBot().getDamageManager().getDamageTicks() < 10) return;
                    List<ItemStack> rods = movementEvent.getBot().getInventoryManager().hasItem(Material.FISHING_ROD);
                    if (rods.isEmpty()) return;

                    //Hold the fishingRod
                    prevSlot.put(movementEvent.getBot().getUniqueId(), movementEvent.getBot().getInventoryManager().getMainHand());
                    movementEvent.getBot().getInventoryManager().setMainHand(movementEvent.getBot().getInventoryManager().getItemSlot(rods.get(0)));
                    movementEvent.getBot().getNpc().dead = false;
                    lastSwitch.put(movementEvent.getBot().getUniqueId(),System.currentTimeMillis());

                    Schedulers
                            .sync()
                            .run(() -> {
                                EntityHuman npc = movementEvent.getBot().getNpc();
                                if (npc != null) {
                                    if (!BuildingUtilitiesKt.isInLineOfSight(
                                            npc.getBukkitEntity().getLocation().getDirection(),
                                            npc.getBukkitEntity().getLocation(),
                                            movementEvent.getBot().getMovements()
                                                    .getTargetLocate()
                                                    .getTargetEntity()
                                                    .getLocation(),
                                            40.0
                                    )) {
                                        return;
                                    }

                                    launchProjectile(
                                            npc,
                                            npc.getBukkitEntity().getLocation(),
                                            movementEvent.getBot().getMovements()
                                                    .getTargetLocate()
                                                    .getTargetEntity()
                                                    .getLocation()
                                    );

                                    movementEvent.getBot().getAttackingManager().setProjectile(true); // Special velocity for the projectile
                                }
                            })
                            .bindWith(bot.getTerminable());
                } else {
                    if (System.currentTimeMillis() - lastSwitch.getOrDefault(movementEvent.getBot().getUniqueId(), 0L) < bot.getProfile().value(RootAccessor.INSTANCE.getRodSettings().getRodDuration())) return;
                    if (mainHand == null) return;
                    if (!mainHand.getType().equals(Material.FISHING_ROD)) return;
                    movementEvent.getBot().getInventoryManager().setMainHand(prevSlot.getOrDefault(movementEvent.getBot().getUniqueId(),0));
                }

            } catch (IllegalArgumentException ignore) {

            }
        }
    }

    public FixedEntityFishingHook launchProjectile(EntityHuman human, Location shooterLocation, Location targetLocation) {
        FixedEntityFishingHook launch = new FixedEntityFishingHook(human.getWorld(), human);

        Validate.notNull(launch, "Projectile not supported");

        ((Projectile) launch.getBukkitEntity()).setVelocity(RodUtilitiesKt.getFishingRodLaunchVelocity(shooterLocation, targetLocation, 1.2));

        human.getWorld().addEntity(launch);
        return launch;
    }
}
