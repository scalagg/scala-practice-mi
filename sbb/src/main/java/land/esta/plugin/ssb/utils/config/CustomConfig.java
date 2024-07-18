package land.esta.plugin.ssb.utils.config;

import land.esta.plugin.ssb.SmartBotsBackend;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class CustomConfig {
    private final FileConfiguration config;
    private final File file;

    public CustomConfig(String name) {
        this.file = new File(SmartBotsBackend.getInstance().getDataFolder(), name + ".yml");
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException var3) {
            }
        }

        this.config = YamlConfiguration.loadConfiguration(this.file);
        this.saveConfig();
    }

    public void saveConfig() {
        try {
            this.config.save(this.file);
        } catch (IOException var2) {
            //Bukkit.broadcastMessage(Color.code("&7Couldnt save file"));
        }

    }
}

