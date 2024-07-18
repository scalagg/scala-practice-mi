package land.esta.plugin.ssb.bots.mechanics.movementsmanagement.submovements;

import lombok.Getter;
import lombok.Setter;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.bots.events.Manager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
@Setter
public class TargetLocate extends Manager {
    private Location target;
    private final Player targetEntity;
    private boolean overwriteLocation;

    private boolean delayedPlayerLocation = true;

    public TargetLocate(Bot bot, Player target) {
        super(bot);
        this.targetEntity = target;
        this.target = targetEntity.getLocation();
        this.overwriteLocation = true;
    }

    @Override
    public void tickPre() {
        if (overwriteLocation) target = bot.getLagCompensatorManager().getClientLocation();
        if (!delayedPlayerLocation && overwriteLocation) target = targetEntity.getLocation();
    }
}
