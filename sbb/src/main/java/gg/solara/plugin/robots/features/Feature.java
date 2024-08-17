package gg.solara.plugin.robots.features;

import gg.solara.plugin.robots.bots.events.EventSubscription;
import gg.solara.plugin.robots.bots.events.Event;
import gg.solara.plugin.robots.bots.events.impl.FinalMoveEvent;
import gg.solara.plugin.robots.bots.Bot;
import gg.solara.plugin.robots.SolaraRobotsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class Feature extends EventSubscription implements Listener {

    public Feature() {
        SolaraRobotsPlugin.getInstance().getEventManager().registerEvent(this);
        Bukkit.getServer().getPluginManager().registerEvents(this, SolaraRobotsPlugin.getInstance());
    }
}
