package gg.solara.plugin.robots.bots.managers;

import gg.solara.plugin.robots.SolaraRobotsPlugin;
import gg.solara.plugin.robots.bot.impl.ParallelKt;
import lombok.Getter;
import gg.solara.plugin.robots.bots.events.Event;
import gg.solara.plugin.robots.bots.Bot;
import gg.solara.plugin.robots.utils.objects.PlayerRunnable;
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
        ParallelKt.getAsyncEventThreadPool().submit(() -> callEvent(event));
    }

    public void callEvent(Event event) {
        var plugin = SolaraRobotsPlugin.getInstance();
        for (var events : plugin.getEventManager().getEvents()) {
            events.call(event);
        }

        plugin.getServer()
                .getPluginManager()
                .callEvent(event);
    }

    public void queueUpdate() {}
    public void tickEarly() {}
    public void tickLate() {}
    public void tick() {}

    public boolean earlyShouldContinue()
    {
        return true;
    }

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
