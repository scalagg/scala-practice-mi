package land.esta.plugin.ssb.bots.events;

import land.esta.plugin.ssb.EstaRobotsPlugin;
import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile;
import lombok.Getter;
import land.esta.plugin.ssb.botevents.BotEvent;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.utils.Color;
import land.esta.plugin.ssb.utils.objects.PlayerRunnable;
import org.bukkit.entity.Player;

@Getter
public class Manager {

    public SettingProfile settingManager;

    public final Bot bot;

    public Manager(Bot bot) {
        this.bot = bot;
        settingManager = bot.getProfile();
    }

    public void globalAction(PlayerRunnable runnable) {
        getBot().getViewsManager().getPlayers().forEach(player -> {
            runnable.run(player, 0);
        });
    }

    public void debug(String string) {
        getBot().getViewsManager().getPlayers().forEach(player -> player.sendMessage(Color.code("&3Debug&7: " + string)));
    }

    public void addPlayerToList(Player player) {}

    public void callEventAsync(Event event) {
        for (BotEvent events : EstaRobotsPlugin.getInstance().getEventManager().getEvents()) {
            events.call(event);
        }
    }

    public void callEvent(Event event) {
        for (BotEvent events : EstaRobotsPlugin.getInstance().getEventManager().getEvents()) {
            events.call(event);
        }
    }


    public void queueUpdate() {}
    public void tickPre() {}
    public void tickPost() {}
    public void tickMid() {}

    public double getDistance() {
        if (!bot.getMovements().getTargetLocate().getTargetEntity().getLocation().getWorld().equals(bot.getMovements().getLocation().getWorld())) return -1;
        return bot.getMovements().getTargetLocate().getTargetEntity().getLocation().distance(bot.getMovements().getLocation());
    }
}
