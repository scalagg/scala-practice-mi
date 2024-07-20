package land.esta.plugin.ssb.support;

import gg.tropic.practice.PracticeGame;
import land.esta.plugin.ssb.SmartBotsBackend;
import lombok.Getter;
import land.esta.plugin.ssb.support.estapractice.EstaPracticeSupport;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

@Getter
public class SupportManager implements Listener {

    private final ArrayList<String> messages = new ArrayList<>();
    private final ArrayList<PluginSupport> pluginSupports = new ArrayList<>();

    public SupportManager() {
        try
        {
            PracticeGame.Companion.getSubCommands();
            addSupport(new EstaPracticeSupport("EstaPractice"));
        } catch (Exception ignored) {

        }

        Bukkit.getServer().getPluginManager().registerEvents(this, SmartBotsBackend.getInstance());
    }

    @EventHandler
    public void joinMessage(PlayerJoinEvent e) {
        messages.forEach(message -> {
            e.getPlayer().sendMessage(message);
        });
    }

    public void addSupport(PluginSupport plugin) {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled(plugin.getName())) {
            pluginSupports.add(plugin.enable());
        }
    }
}
