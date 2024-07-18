package land.esta.plugin.ssb.bots.setting;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public class PacketsSettings extends Setting {
    private final SettingObject relativeMove = new SettingObject("Relative Move",Material.STRING,false);

    public PacketsSettings() {
        super(Material.STRING,"&c&lPackets Settings","Packets", "packetssettings",0,false,"&f In this category","&f you are able to","&f modify the bot's","&f packets settings.","","&aClick to select!");

        addSetting(relativeMove);
    }
}
