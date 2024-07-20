package land.esta.plugin.ssb.support;

import lombok.Getter;
import land.esta.plugin.ssb.support.estapractice.EstaPracticeSupport;
import net.evilblock.cubed.util.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.ArrayList;

@Getter
public class SupportManager implements Listener {

    private final ArrayList<PluginSupport> pluginSupports = new ArrayList<>();

    public SupportManager() {
        if (Reflection.getClassSuppressed("gg.tropic.practice.PracticeGame") != null)
        {
            addSupport(new EstaPracticeSupport("EstaPractice"));
        }
    }

    public void addSupport(PluginSupport plugin) {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled(plugin.getName())) {
            pluginSupports.add(plugin.enable());
        }
    }
}
