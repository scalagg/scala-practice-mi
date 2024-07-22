package land.esta.plugin.ssb.bots.setting;

import lombok.*;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Setting {

    public final ArrayList<SettingObject> settings = new ArrayList<>();
    private final Material material = Material.PAPER;

    public final String fancyName;
    public final String name;

    private int id = 0;
    private int slot;

    private List<String> description;
    private boolean important;
    private List<Integer> empty;

    public static final HashMap<String,Setting> defaults = new HashMap<>();

    public void addSetting(SettingObject object) {
        settings.add(object);
    }

    public void addSettings(SettingObject... objects) {
        int index = 0;
        for (SettingObject object : objects) {
            if (object == null) {
                empty.add(index);
                continue;
            }

            addSetting(object);
            index++;
        }
    }
    public void addSettings(ArrayList<SettingObject> objects) {
        for (SettingObject object : objects) {
            addSetting(object);
        }
    }

    public Setting clone() {
        Setting setting = new Setting(material, fancyName,name, permissionName, slot,important,description);
        setting.addSettings(settings);

        return setting;
    }
}
