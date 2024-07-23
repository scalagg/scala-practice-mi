package land.esta.plugin.ssb.support;

import gg.tropic.practice.player.hotbar.LobbyHotbarService;
import land.esta.plugin.ssb.bot.menu.BotDuelMenu;
import lombok.Getter;
import land.esta.plugin.ssb.support.estapractice.EstaPracticeSupport;
import net.evilblock.cubed.util.Reflection;
import org.bukkit.Bukkit;

import java.util.ArrayList;

@Getter
public class SupportManager {

    private final ArrayList<PluginSupport> pluginSupports = new ArrayList<>();

    public SupportManager() {
        if (Reflection.getClassSuppressed("gg.tropic.practice.PracticeGame") != null)
        {
            addSupport(new EstaPracticeSupport("EstaPractice"));
        }

        if (Reflection.getClassSuppressed("gg.tropic.practice.PracticeLobby") != null)
        {
            LobbyHotbarService.INSTANCE.setBuildNewBotFightMenu((player) -> new BotDuelMenu());
        }
    }

    public void addSupport(PluginSupport plugin) {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled(plugin.getName())) {
            pluginSupports.add(plugin.enable());
        }
    }
}
