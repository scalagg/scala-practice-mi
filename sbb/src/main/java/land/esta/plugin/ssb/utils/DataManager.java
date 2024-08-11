package land.esta.plugin.ssb.utils;

import land.esta.plugin.ssb.SolaraRobotsPlugin;
import me.lucko.helper.Events;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author GrowlyX
 * @since 7/22/2024
 */
public class DataManager {
    private final Map<UUID, DataPlayer> players = new HashMap<>();
    public DataManager(SolaraRobotsPlugin plugin) {
        Events
                .subscribe(PlayerJoinEvent.class)
                .handler(event -> {
                    players.put(
                            event.getPlayer().getUniqueId(),
                            new DataPlayer(event.getPlayer())
                    );
                })
                .bindWith(plugin);

        Events
                .subscribe(PlayerQuitEvent.class)
                .handler(event -> {
                    players.remove(event.getPlayer().getUniqueId());
                })
                .bindWith(plugin);
    }

    public @Nullable DataPlayer getPlayer(@NotNull Player player) {
        return players.get(player.getUniqueId());
    }
}
