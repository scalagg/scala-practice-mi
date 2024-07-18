package land.esta.plugin.ssb.features;

import land.esta.plugin.ssb.botevents.BotEvent;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.botevents.events.FinalMoveEvent;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.bots.setting.SettingManager;
import land.esta.plugin.ssb.SmartBotsBackend;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class Feature extends BotEvent implements Listener {

    public Feature() {
        SmartBotsBackend.getInstance().getEventManager().registerEvent(this);
        Bukkit.getServer().getPluginManager().registerEvents(this, SmartBotsBackend.getInstance());
    }

    public Feature(Plugin plugin) {
        SmartBotsBackend.getInstance().getEventManager().registerEvent(this);
        Bukkit.getServer().getPluginManager().registerEvents(this,plugin);
    }

    /*  Known events */

    public void listen(Event event) {
        if (event instanceof FinalMoveEvent) {
            FinalMoveEvent e = (FinalMoveEvent) event;
            finalMoveEvent(e, event.bot,event.bot.getSettingManager());
        }
    }

    public void finalMoveEvent(FinalMoveEvent event, Bot bot, SettingManager setting) {}
}
