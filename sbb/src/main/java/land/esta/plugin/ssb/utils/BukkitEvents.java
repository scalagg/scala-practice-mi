package land.esta.plugin.ssb.utils;

import lombok.Getter;
import land.esta.plugin.ssb.SmartBotsBackend;

@Getter
public class BukkitEvents {
    private final boolean projectileHitEvent = FileManager.getOrDefault(SmartBotsBackend.getInstance(),"bukkitevents.projectileHitEvent",true);
}
