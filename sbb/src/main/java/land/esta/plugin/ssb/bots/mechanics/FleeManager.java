package land.esta.plugin.ssb.bots.mechanics;

import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import lombok.Getter;
import lombok.Setter;
import land.esta.plugin.ssb.botevents.events.RunningEvent;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.bots.events.Manager;
import land.esta.plugin.ssb.bots.mechanics.movementsmanagement.submovements.TargetLocate;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@Getter
@Setter
public class FleeManager extends Manager {

    private boolean active;
    private double clcBlocks = getBot().getProfile().value(RootAccessor.INSTANCE.getFleeSettings().getCalculatedDistance());
    private boolean enabled = true;
    private int ticks;
    private int offTicks;

    private boolean running;
    private boolean runningFromCombo;

    public FleeManager(Bot bot) {
        super(bot);
    }

    //TODO if blocks in way

    @Override
    public void tickEarly() {
        //Active
        TargetLocate finder = getBot().getMovements().getTargetLocate();
        active = check();

        //Ticks system
        if (active) {
            ticks++;
            offTicks = 0;
        }
        if (!active) {
            ticks = 0;
            offTicks++;
        }

        //Event
        if (running) {
            RunningEvent runningEvent;
            callEvent(runningEvent = new RunningEvent(getBot(), ticks));
            if (runningEvent.isForceEnd()) {
                running = false;
                active = false;
            }
        }

        //Running system
        finder.setOverwriteLocation(!active);

        if (!active) return;
        Vector direction = getBot().getMovements().getLocation().toVector().subtract(finder.getTargetEntity().getLocation().toVector()).setY(0).normalize();
        Location oppositeLoc = direction.multiply(clcBlocks).add(getBot().getMovements().getLocation().toVector()).toLocation(getBot().getMovements().getLocation().getWorld());

        finder.setTarget(oppositeLoc);
    }

    public boolean check() {
        if (!enabled) return false;
        return getBot().getInventoryManager().isRequiredToEat() ||
                running || getBot().getInventoryManager().isEating() ||
                runningFromCombo;
    }
}
