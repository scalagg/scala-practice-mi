package land.esta.plugin.ssb.bots.events;

import land.esta.plugin.ssb.EstaRobotsPlugin;
import land.esta.plugin.ssb.bot.impl.ParallelKt;
import lombok.Getter;
import land.esta.plugin.ssb.botevents.BotEvent;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.utils.objects.PlayerRunnable;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class Manager {

    protected final Bot bot;

    public void globalAction(PlayerRunnable runnable) {
        getBot().getViewsManager().getPlayers().forEach(player -> runnable.run(player, 0));
    }

    public void addPlayerToList(Player player) {}

    public void callEventAsync(Event event) {
        ParallelKt.getAsyncEventThreadPool().submit(() -> {
            for (BotEvent events : EstaRobotsPlugin.getInstance().getEventManager().getEvents()) {
                events.call(event);
            }
        });
    }

    public void callEvent(Event event) {
        for (BotEvent events : EstaRobotsPlugin.getInstance().getEventManager().getEvents()) {
            events.call(event);
        }
    }

    public void queueUpdate() {}
    public void tickEarly() {}
    public void tickLate() {}
    public void tick() {}

    public double getDistance() {
        var location = bot.getMovements().getTargetLocate().getTargetEntity().getLocation();
        var expectedLocation = bot.getMovements().getLocation();
        if (!location.getWorld().equals(expectedLocation.getWorld()))
        {
            return -1;
        }

        return location.distance(expectedLocation);
    }
}
