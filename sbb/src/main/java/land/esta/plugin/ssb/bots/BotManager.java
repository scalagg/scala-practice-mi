package land.esta.plugin.ssb.bots;

import land.esta.plugin.ssb.utils.GameServer;
import lombok.Getter;
import land.esta.plugin.ssb.EstaRobotsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@Getter
public class BotManager {
    private final ArrayList<Bot> bots;
    @Nullable
    private BukkitTask task;
    private final boolean async = false;

    public BotManager() {
        bots = new ArrayList<>();

        if (GameServer.is()) {
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    ArrayList<Bot> cloned = new ArrayList<>(bots);
                    for (Bot bot : cloned) {
                        if (bot == null) {
                            //Bukkit.broadcastMessage("A problem was found");
                            continue;
                        }

                        if (!bot.getPlayer().isOnline()) {
                            continue;
                        }

                        // Cannot measure distance between diff words patch
                        if (bot.getMovements().getLocation().getWorld().equals(bot.getMovements().getTargetLocate().getTarget().getWorld()) && bot.isAlive()) {
                            bot.tick();
                            continue;
                        }
                        bot.silentKill();
                    }
                }
            }.runTaskTimer(EstaRobotsPlugin.getInstance(), 1, 1);
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
