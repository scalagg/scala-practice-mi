package land.esta.plugin.ssb.features.imple;

import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import land.esta.plugin.ssb.botevents.events.BotDeathEvent;
import lombok.Getter;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.botevents.events.BotDamageEvent;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.botevents.events.BotAttackEvent;
import land.esta.plugin.ssb.features.Feature;
import land.esta.plugin.ssb.EstaRobotsPlugin;
import me.lucko.helper.Schedulers;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class StrafeFeature extends Feature {
    private final Map<UUID, Float> strafe = new HashMap<>();
    private final Map<UUID, Integer> ticks = new HashMap<>();
    private final Map<UUID, Integer> combo = new HashMap<>();

    @EventHandler
    public void deathEvent(BotDeathEvent event) {
        var uniqueId = event.getBot().getUniqueId();
        strafe.remove(uniqueId);
        ticks.remove(uniqueId);
        combo.remove(uniqueId);
    }

    @Override
    public void tickEvent(Bot bot) {
        //Values
        boolean inCombo = combo.getOrDefault(bot.getUniqueId(), 0) > 3;

        // Check if the bot and target are in the same world
        Location botLocation = bot.getMovements().getLocation();
        Location targetLocation = bot.getMovements().getTargetLocate().getTargetEntity().getLocation();
        if (!botLocation.getWorld().equals(targetLocation.getWorld())) {
            return;
        }

        boolean close = botLocation.distance(targetLocation) < 6;
        boolean chance = ThreadLocalRandom.current().nextInt(100) <= bot.getProfile().value(RootAccessor.INSTANCE.getStrafeSettings().getChance());

        //Enable & Combo & Close check
        if (!bot.getProfile().value(RootAccessor.INSTANCE.getStrafeSettings().getEnabled()) || inCombo && !bot.getProfile().value(RootAccessor.INSTANCE.getStrafeSettings().getStrafeInCombo()) || !close || bot.getMovements().isEdge()) {
            strafe.put(bot.getUniqueId(), 0f);
            bot.getMovements().setStrafe(0);
            return;
        }

        if (!chance) {
            bot.getMovements().setStrafe(0);
            return;
        }

        //Defaults + ticking
        if (strafe.getOrDefault(bot.getUniqueId(), 0f) == 0) strafe.put(bot.getUniqueId(), 45f);
        ticks.put(bot.getUniqueId(), ticks.getOrDefault(bot.getUniqueId(), 0) + 1);

        //Switch check
        if (ticks.get(bot.getUniqueId()) > bot.getProfile().value(RootAccessor.INSTANCE.getStrafeSettings().getSwitchSideTicks())) {
            ticks.put(bot.getUniqueId(), 0);
            strafe.put(bot.getUniqueId(), strafe.getOrDefault(bot.getUniqueId(), 0f) * -1f);
            bot.getMovements().setSpeedMultiplier(1 / 1.05);
        }

        bot.getMovements().setStrafe(strafe.get(bot.getUniqueId()));
    }

    @Override
    public void listen(Event event) {
        Bot bot = event.getBot();
        //Combo calculation.
        if (event instanceof BotDamageEvent) {
            combo.put(event.getBot().getUniqueId(), combo.getOrDefault(event.getBot().getUniqueId(), 0) + 1);
            if (combo.get(event.getBot().getUniqueId()) > bot.getProfile().value(RootAccessor.INSTANCE.getFleeSettings().getRunFromCombosHits()) && bot.getProfile().value(RootAccessor.INSTANCE.getFleeSettings().getRunFromCombos()) && !event.getBot().getFleeManager().isRunning()) {
                event.getBot().getFleeManager().setRunningFromCombo(true);
                combo.put(event.getBot().getUniqueId(), 0);

                var time = bot.getProfile().value(RootAccessor.INSTANCE.getFleeSettings().getRunFromCombosTime());
                Schedulers
                        .sync()
                        .runLater(() -> event.getBot().getFleeManager().setRunningFromCombo(false), time)
                        .bindWith(event.getBot().getTerminable());
            }
        }
        if (event instanceof BotAttackEvent) {
            combo.put(event.getBot().getUniqueId(), 0);
        }
    }
}
