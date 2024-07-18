package land.esta.plugin.ssb.utils;

import org.bukkit.ChatColor;

public class Color {
    public static String code(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
