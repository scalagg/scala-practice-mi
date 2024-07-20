package land.esta.plugin.ssb.bots.mechanics.movementsmanagement;

import land.esta.plugin.ssb.SmartBotsBackend;
import land.esta.plugin.ssb.bots.mechanics.movementsmanagement.submovements.*;
import lombok.Getter;
import lombok.Setter;
import land.esta.plugin.ssb.botevents.events.FinalMoveEvent;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.bots.events.Manager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@Getter
@Setter
public class MovementsMain extends Manager {
    private Location location;
    private float strafe = 0;
    private Vector lastMotion = new Vector(0,0,0);
    private TargetLocate targetLocate;
    private boolean sentLocationUpdate;

    private boolean wtap;
    private boolean use;
    private int disableJumps = 0;

    private boolean onGround = true;

    private boolean attackSprint = true;
    private boolean reset = false;
    private boolean sneak = false;
    private boolean sprint = true;
    private float friction = 0.91f;
    private double motionY;
    private boolean move = true;
    private boolean edge;

    private Movements movements;
    private Collision collision;
    private RecodedCollision recodedCollision;
    private Rotations rotations;
    private boolean jump;
    private double attackMultiplier = 1;
    private double speedMultiplier;

    private boolean edgeDetector;
    private int groundTicks;
    private boolean requiredToMove = true;
    private boolean jumpCombo = false;

    public MovementsMain(Bot bot, Player player, Location loc) {
        super(bot);
        location = loc;

        bot.getMEventManager().addEvent(collision = new Collision(bot));
        bot.getMEventManager().addEvent(recodedCollision = new RecodedCollision(bot));
        bot.getMEventManager().addEvent(movements = new Movements(bot));
        bot.getMEventManager().addEvent(rotations = new Rotations(bot));
        bot.getMEventManager().addEvent(targetLocate = new TargetLocate(bot, player));
    }

    private boolean delayedBotLocation = bot.getSettingManager().getConnectionSettings().getDelayedBotLocation().isBooleanValue();

    @Override
    public void tickMid() {
        if (sentLocationUpdate) {
            if (delayedBotLocation) {
                new BukkitRunnable() {
                    final Location location = getBot().getMovements().getLocation();

                    public void run() {
                        getBot().getPacketsManager().updateLocation(location);
                    }
                }.runTaskLater(SmartBotsBackend.getInstance(), settingManager.getConnectionSettings().getPing().getIntValue() / 100);
            } else {
                getBot().getPacketsManager().updateLocation(getBot().getMovements().getLocation());
            }
        }
        sentLocationUpdate = false;
        attackMultiplier = 1;
        speedMultiplier = 1;
    }

    public void move(Vector motion,boolean addLastMotion) {
        disableJumps = Math.min(disableJumps - 1,0);

        Location oldLoc = location.clone();
        Vector loc = location.toVector();

        //Attack slowdown
        lastMotion.multiply(new Vector(bot.getMovements().getAttackMultiplier(),1,bot.getMovements().getAttackMultiplier()));
        if (addLastMotion) motion.add(lastMotion.clone().setY(0).multiply(friction));
        loc.add(motion);

        Location newLoc = loc.toLocation(location.getWorld());
        Location updatedLoc;

        updatedLoc = recodedCollision.cast(oldLoc.clone(),newLoc.clone());


        updatedLoc.setYaw(location.getYaw());
        updatedLoc.setPitch(location.getPitch());

        callEventAsync(new FinalMoveEvent(bot,updatedLoc,location));

        location = updatedLoc;

        Vector offSet = location.toVector().clone().subtract(oldLoc.toVector().clone());
        motion = offSet;
        motionY = offSet.getY();

        lastMotion = motion.clone();
        updatedLoc();

        if (use || !sprint || !move) reset = true;
        if (reset && (!use || sprint || move)) {
            setAttackSprint(true);
            reset = false;
        }
        bot.getNpc().onGround = onGround;

        collision.runWaterCheck();
    }

    public void setAttackSprint(boolean sprint) {
        this.sprint = sprint;
        //Nms update
        new BukkitRunnable() {
            @Override
            public void run() {
                bot.getNpc().setSprinting(attackSprint);
            }
        }.runTask(SmartBotsBackend.getInstance());
    }

    public void attack() {
        if (sprint && !jumpCombo) attackMultiplier *= 0.6;
    }

    public void updatedLoc() {
        sentLocationUpdate = true;
    }

    public void rotate(float yaw,float pitch) {
        location.setYaw(yaw);
        location.setPitch(pitch);
        updatedLoc();
    }
}
