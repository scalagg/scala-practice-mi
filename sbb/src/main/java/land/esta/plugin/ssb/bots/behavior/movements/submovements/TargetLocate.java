package land.esta.plugin.ssb.bots.behavior.movements.submovements;

import land.esta.plugin.ssb.bots.BotUtilitiesKt;
import lombok.Getter;
import lombok.Setter;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.bots.managers.Manager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class TargetLocate extends Manager {
    private Location target;
    private boolean overwriteLocation;
    private @Nullable Player targetEntity;

    private boolean delayedPlayerLocation = true;
    private Location aStarTarget = null;

    public TargetLocate(Bot bot) {
        super(bot);
        this.overwriteLocation = true;
    }

    @Override
    public boolean earlyShouldContinue() {
        this.targetEntity = BotUtilitiesKt.getTargetEntity(bot);
        if (overwriteLocation) {
            target = bot.getLagCompensatorManager().getClientLocation();
        }

        if (!delayedPlayerLocation && overwriteLocation) {
            if (targetEntity != null) {
                target = targetEntity.getLocation();
            }
        }
        return targetEntity != null;
    }
}
