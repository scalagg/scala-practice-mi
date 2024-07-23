package land.esta.plugin.ssb.bots;

import land.esta.plugin.ssb.utils.GameServer;
import lombok.Getter;
import land.esta.plugin.ssb.EstaRobotsPlugin;
import me.lucko.helper.Schedulers;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Getter
public class BotManager {

    public final ArrayList<Bot> bots = new ArrayList<>();

    public BotManager(EstaRobotsPlugin plugin) {
        if (GameServer.is()) {
            Schedulers.sync()
                    .runRepeating(() -> {
                        final ArrayList<Bot> cloned = new ArrayList<>(bots);
                        for (Bot bot : cloned) {
                            if (bot == null) {
                                continue;
                            }

                            if (!bot.getPlayer().isOnline()) {
                                continue;
                            }

                            // Cannot measure distance between diff words patch
                            if (
                                    bot.getMovements().getLocation().getWorld().equals(
                                            bot.getMovements().getTargetLocate().getTarget().getWorld()
                                    ) &&
                                            bot.isAlive()
                            ) {
                                bot.tick();
                                continue;
                            }

                            bot.silentKill();
                        }
                    }, 1L, 1L)
                    .bindWith(plugin);
        }
    }

    public void addBot(Bot bot) {
        bots.add(bot);
    }

    public void removeBot(Bot bot) {
        bots.remove(bot);
    }

    public Bot getNpcFromId(int id) {
        for (Bot npc : bots) {
            if (npc.getNpc().getBukkitEntity().getEntityId() == id) {
                return npc;
            }
        }

        return null;
    }

    public Bot getNpcFromTarget(Player target) {
        for (Bot npc : bots) {
            if (npc.getMovements().getTargetLocate().getTargetEntity().getUniqueId() == target.getUniqueId()) {
                return npc;
            }
        }

        return null;
    }

    public Bot getNpcFromTargetView(Player target) {
        for (Bot npc : bots) {
            if (npc.getViewsManager().getPlayers().contains(target)) {
                return npc;
            }
        }

        return null;
    }
}
