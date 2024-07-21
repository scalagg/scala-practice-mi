package land.esta.plugin.ssb.data;

import land.esta.plugin.ssb.SmartBotsBackend;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

@Getter
public class DataManager implements Listener {

    private final Map<Player, DataPlayer> dataSet;

    public DataManager() {
        dataSet = new HashMap<>();
    }

    @EventHandler
    public void add(PlayerJoinEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                SmartBotsBackend.getInstance().getMongoHandler().createBotDoc(e.getPlayer().getUniqueId());
            }
        }.runTaskAsynchronously(SmartBotsBackend.getInstance());

        DataPlayer player = new DataPlayer(e.getPlayer());
        dataSet.put(e.getPlayer(), player);

        SmartBotsBackend.getInstance().getBotManager().getBots().forEach(bot -> {
            e.getPlayer().hidePlayer(bot.getNpc().getBukkitEntity());
        });
    }

    @EventHandler
    public void remove(PlayerQuitEvent e) {
        try {
            getPlayer(e.getPlayer()).quit();
            dataSet.remove(e.getPlayer());
        } catch (Exception ignored) {

        }
    }

    public DataPlayer getPlayer(Player player) {
        return dataSet.get(player);
    }
}
