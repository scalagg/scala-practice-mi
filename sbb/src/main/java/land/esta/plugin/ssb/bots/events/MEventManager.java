package land.esta.plugin.ssb.bots.events;

import land.esta.plugin.ssb.EstaRobotsPlugin;
import land.esta.plugin.ssb.botevents.BotEvent;
import land.esta.plugin.ssb.bots.Bot;
import me.lucko.helper.Schedulers;

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

    public void tickEarly() {
        try {
            managers.forEach(Manager::tickEarly);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void tickLate() {
        try {
            managers.forEach(Manager::tickLate);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void tick() {
        try {
            managers.forEach(Manager::tick);
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
