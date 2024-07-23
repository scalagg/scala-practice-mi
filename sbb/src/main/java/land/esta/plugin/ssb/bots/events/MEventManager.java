package land.esta.plugin.ssb.bots.events;

import land.esta.plugin.ssb.EstaRobotsPlugin;
import land.esta.plugin.ssb.botevents.BotEvent;
import land.esta.plugin.ssb.bots.Bot;
import me.lucko.helper.Schedulers;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MEventManager {

    private final Bot bot;
    private final ArrayList<Manager> managers = new ArrayList<>();

    public MEventManager(Bot bot) {
        this.bot = bot;
    }

    public void addEvent(Manager manager) {
        managers.add(manager);
    }

    public void queueUpdate() {
        try {
            managers.forEach(Manager::queueUpdate);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void tickPre() {
        try {
            managers.forEach(Manager::tickPre);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void tickPost() {
        try {
            managers.forEach(Manager::tickPost);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void tickMid() {
        try {
            managers.forEach(Manager::tickMid);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void callEvents() {
        // Run async
        Schedulers
                .async()
                .run(() -> {
                    List<BotEvent> botEvents = EstaRobotsPlugin.getInstance().getEventManager().getEvents();
                    for (BotEvent event : botEvents) {
                        event.callTick(bot);
                    }
                })
                .bindWith(bot.getTerminable());
    }
}
