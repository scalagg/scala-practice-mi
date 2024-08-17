package gg.solara.plugin.robots.bots;

import gg.solara.plugin.robots.utils.GameServer;
import lombok.Getter;
import gg.solara.plugin.robots.SolaraRobotsPlugin;
import me.lucko.helper.Schedulers;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class BotManager {

    private final ConcurrentHashMap<Integer, Bot> bots = new ConcurrentHashMap<>();
    public BotManager(SolaraRobotsPlugin plugin) {
        if (GameServer.is()) {
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

    public Bot getNpcFromTarget(Player target) {
        for (Bot npc : bots.values()) {
            if (npc.getMovements().getTargetLocate().getTargetEntity().getUniqueId() == target.getUniqueId()) {
                return npc;
            }
        }

        return null;
    }
}
