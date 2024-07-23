package land.esta.plugin.ssb.bots.mechanics.movementsmanagement;

import land.esta.plugin.ssb.EstaRobotsPlugin;
import land.esta.plugin.ssb.bot.pathfinding.AStarPathfinder;
import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
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

import java.util.List;
import java.util.Random;

@Getter
@Setter
public class MovementsMain extends Manager {
    public Location location;
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

    private long last = System.currentTimeMillis();
    private int times = 0;

    private Random random = new Random();

    public MovementsMain(Bot bot, Player player, Location loc) {
        super(bot);
        location = loc;

        bot.getEventManager().addEvent(collision = new Collision(bot));
        bot.getEventManager().addEvent(recodedCollision = new RecodedCollision(bot));
        bot.getEventManager().addEvent(movements = new Movements(bot));
        bot.getEventManager().addEvent(rotations = new Rotations(bot));
        bot.getEventManager().addEvent(targetLocate = new TargetLocate(bot, player));
    }

    private boolean delayedBotLocation = bot.getProfile().value(RootAccessor.INSTANCE.getConnectionSettings().getDelayedBotLocation());

    @Override
    public void tickMid() {
        if (sentLocationUpdate) {
            if (delayedBotLocation) {
                new BukkitRunnable() {
                    final Location location = getBot().getMovements().getLocation();

                    public void run() {
                        getBot().getPacketsManager().updateLocation(location);
                    }
                }.runTaskLater(EstaRobotsPlugin.getInstance(), bot.getProfile().value(RootAccessor.INSTANCE.getConnectionSettings().getPing()) / 100);
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

        float yaw = location.getYaw();
        float pitch = location.getPitch();

        if (isOnGround() && oldLoc.distance(updatedLoc) < 0.2 && oldLoc.distance(bot.getStartLocation()) > bot.getProfile().value(RootAccessor.INSTANCE.getPathfindingSetting().getDistanceFromLastLocation()) && System.currentTimeMillis() - bot.getProfile().value(RootAccessor.INSTANCE.getPathfindingSetting().getGetPathCooldown()) > last && updatedLoc.distance(targetLocate.getTargetEntity().getLocation()) > bot.getProfile().value(RootAccessor.INSTANCE.getPathfindingSetting().getMinDistanceFromPlayer()) && System.currentTimeMillis() - bot.getProfile().value(RootAccessor.INSTANCE.getPathfindingSetting().getCooldownAfterAttack()) > bot.getAttackingManager().getAttacked()) {
            last = System.currentTimeMillis();

            TargetLocate target = bot.getMovements().getTargetLocate();
            AStarPathfinder aStar = new AStarPathfinder(bot.getNpc().getBukkitEntity().getLocation(), target.getTargetEntity().getLocation(), 1000, false, 100);

            List<Location> path = aStar.getPath();

            if (path.isEmpty()) {
                rotate(random.nextInt(-180, 180), 0);

                updatedLoc.setYaw(yaw);
                updatedLoc.setPitch(pitch);

                target.setAStarTarget(updatedLoc);
                return;
            }

            target.setTarget(path.get(0));
            updatedLoc = path.get(0);

            // x = -90, -x = 90, z = 0, -z = -180
            int index = 0;
            while (true) {
                if (path.size() - 1 <= index) {
                    yaw = random.nextInt(-180, 180);
                    break;
                }
                Location l = path.get(index);
                if (l.getX() > location.getX()) {
                    yaw = -90;
                    updatedLoc = l;
                    break;
                } else if (l.getX() < location.getX()) {
                    yaw = 90;
                    updatedLoc = l;
                    break;
                } else if (l.getZ() > location.getZ()) {
                    yaw = 0;
                    updatedLoc = l;
                    break;
                } else if (l.getZ() < location.getZ()) {
                    yaw = -180;
                    updatedLoc = l;
                    break;
                } else {
                    index++;
                }
            }

            pitch = 0;

            rotate(yaw, pitch);

            updatedLoc.setYaw(yaw);
            updatedLoc.setPitch(pitch);

            target.setAStarTarget(updatedLoc);

            return;
        } else {
            if (times > 1) {
                targetLocate.setAStarTarget(null);
                times = 0;
            }
            else times++;
        }

        updatedLoc.setYaw(yaw);
        updatedLoc.setPitch(pitch);

        callEventAsync(new FinalMoveEvent(bot,updatedLoc,location));

        location = updatedLoc;

        rotate(yaw, pitch);

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
        }.runTask(EstaRobotsPlugin.getInstance());
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
