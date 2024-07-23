package land.esta.plugin.ssb.features;

import land.esta.plugin.ssb.botevents.BotEvent;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.botevents.events.FinalMoveEvent;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.EstaRobotsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class Feature extends BotEvent implements Listener {

    public Feature() {
        EstaRobotsPlugin.getInstance().getEventManager().registerEvent(this);
        Bukkit.getServer().getPluginManager().registerEvents(this, EstaRobotsPlugin.getInstance());
    }

    public Feature(Plugin plugin) {
        EstaRobotsPlugin.getInstance().getEventManager().registerEvent(this);
        Bukkit.getServer().getPluginManager().registerEvents(this,plugin);
    }

    /*  Known events */
    public void listen(Event event) {
        if (event instanceof FinalMoveEvent e) {
            finalMoveEvent(e, event.bot);
        }
    }

    public void finalMoveEvent(FinalMoveEvent event, Bot bot) {}
}
