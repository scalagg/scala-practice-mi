package gg.solara.plugin.robots.bots;

import lombok.Getter;
import gg.solara.plugin.robots.SolaraRobotsPlugin;
import me.lucko.helper.Schedulers;

import java.util.concurrent.ConcurrentHashMap;

@Getter
public class BotManager {

    private final ConcurrentHashMap<Integer, Bot> bots = new ConcurrentHashMap<>();
    public BotManager(SolaraRobotsPlugin plugin) {
        Schedulers.sync()
                .runRepeating(() -> {
                    for (Bot bot : bots.values()) {
                        if (bot.isAlive()) {
                            bot.tick();
                        }
                    }
                }, 0L, 1L)
                .bindWith(plugin);
    }

    public void addBot(Bot bot) {
        bots.put(
                bot.getNpc().getId(),
                bot
        );
    }

    public void removeBot(Bot bot) {
        bots.remove(bot.getNpc().getId());
    }
}
