package land.esta.plugin.ssb.bots.mechanics;

import land.esta.plugin.ssb.SmartBotsBackend;
import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import lombok.Getter;
import lombok.Setter;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.bots.events.Manager;
import land.esta.plugin.ssb.utils.objects.DataLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class LagCompensatorManager extends Manager {

    private List<DataLocation> locations;
    private int maxLocations = 20;
    private UUID lastPlayer;

    public Player getPlayer() {
        if (bot.getMovements() == null) return null;
        if (bot.getMovements().getTargetLocate() == null) return null;
        return bot.getMovements().getTargetLocate().getTargetEntity();
    }

    public LagCompensatorManager(Bot bot) {
        super(bot);
        locations = new ArrayList<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!bot.isAlive()) {
                    this.cancel();
                    return;
                }
                Player p = getPlayer();
                if (p == null) return;
                if (lastPlayer == null) lastPlayer = p.getUniqueId();
                if (lastPlayer != p.getUniqueId()) locations.clear();

                lastPlayer = p.getUniqueId();
                movePacket(p.getLocation());
            }
        }.runTaskTimer(SmartBotsBackend.getInstance(),1,1);
    }

    public void movePacket(Location loc) {
        if (loc.getX() == 0 && loc.getY() == 0 && loc.getZ() == 0) return;
        locations.add(new DataLocation(loc));

        if (locations.size() > maxLocations) locations.remove(locations.get(0));
    }

    public DataLocation getPrevLocation(long time) {
        DataLocation orElse = new DataLocation(getPlayer().getLocation());
        if (locations.size() > 0) orElse = locations.get(locations.size() - 1);

        long min = Long.MAX_VALUE;
        DataLocation result = orElse;
        for (int i = 0; i < locations.size();i++) {
            DataLocation loc = locations.get(i);
            long value = Math.abs(loc.getTime() - (System.currentTimeMillis() - time));
            if (value < min) {
                min = value;
                result = loc;
            }
        }

        return result;
    }

    public Location getClientLocation() {
        return getPrevLocation(bot.getProfile().value(RootAccessor.INSTANCE.getConnectionSettings().getPing()) + bot.getProfile().value(RootAccessor.INSTANCE.getConnectionSettings().getConnectionPacketDelay())).toLocation(bot.getMovements().getLocation().getWorld());
    }
}
