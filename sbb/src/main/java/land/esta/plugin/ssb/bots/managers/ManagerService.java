package land.esta.plugin.ssb.bots.managers;

import land.esta.plugin.ssb.SolaraRobotsPlugin;
import land.esta.plugin.ssb.botevents.BotEvent;
import land.esta.plugin.ssb.bots.Bot;

import java.util.ArrayList;
import java.util.List;

public class ManagerService {

    private final Bot bot;
    private final ArrayList<Manager> managers = new ArrayList<>();

    public ManagerService(Bot bot) {
        this.bot = bot;
    }

    public void addManager(Manager manager) {
        managers.add(manager);
    }

    public boolean ensureReady() {
        for (Manager manager : managers) {
            if (!manager.earlyShouldContinue()) {
                return false;
            }
        }

        return true;
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

    public void executeEvents() {
        // Run async
        List<BotEvent> botEvents = SolaraRobotsPlugin.getInstance().getEventManager().getEvents();
        for (BotEvent event : botEvents) {
            event.execute(bot);
        }
    }
}
