package land.esta.plugin.ssb.bots.mechanics.movementsmanagement.submovements;

import land.esta.plugin.ssb.bot.pathfinding.AStarPathfinder;
import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.bots.events.Manager;
import land.esta.plugin.ssb.utils.BlockManager;
import land.esta.plugin.ssb.utils.RayTrace;
import land.esta.plugin.ssb.utils.objects.AABB;
import land.esta.plugin.ssb.utils.objects.CraftiFriction;
import net.evilblock.cubed.entity.npc.pathfinding.AStar;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.List;

public class Movements extends Manager {
    public Movements(Bot bot) {
        super(bot);
    }

    @Override
    public void tickPre() {
        //boolean requireToMove = bot.getMovements().getLocation().distanceSquared(bot.getMovements().getTargetLocate().getTarget()) >=Math.pow(settingManager.getStrafeSettings().getDistance().getDoubleValue(), 2);
        //bot.getMovements().setRequiredToMove(requireToMove);
        //if (!requireToMove) bot.getAttackingManager().setHitSelecting(false);


        move();
    }

    private final double blockingMultiplier = 0.2;
    private final boolean advancedReduce = bot.getProfile().value(RootAccessor.INSTANCE.getAttackSettings().getAdvancedReduce());

    private boolean lastOnGround;

    public void move() {
        Vector motion = getDirection(bot.getMovements().getLocation().getYaw() + bot.getMovements().getStrafe(),0);
        float friction = bot.getMovements().getFriction();

        //The default movement speed
        double movementSpeed = 0.1;
        int speed = bot.getEffectsManager().getLevel(PotionEffectType.SPEED);
        movementSpeed += 0.02 * speed;

        if (bot.getMovements().isOnGround()) {
            if (bot.getMovements().isSprint()) movementSpeed *= 1.3;
            movementSpeed *= (0.16277136f / Math.pow((friction), 3));

            if (bot.getMovements().isJump() && bot.getMovements().getDisableJumps() <= 0) movementSpeed += 0.2;
        } else {
            movementSpeed = 0.02;
            if (bot.getMovements().isSprint()) movementSpeed = 0.026;
            bot.getMovements().setFriction(0.91f);
            motion.setY((bot.getMovements().getMotionY() - 0.08D) * 0.9800000190734863D);
        }

        bot.getMovements().setFriction(friction);

        if (bot.getMovements().isUse()) movementSpeed *= blockingMultiplier;
        if (!bot.getMovements().isMove() || !bot.getMovements().isRequiredToMove()) movementSpeed *= 0;
        movementSpeed *= bot.getMovements().getSpeedMultiplier();

        motion.setX(motion.getX() * movementSpeed);
        motion.setZ(motion.getZ() * movementSpeed);
        boolean velocityTaken = false;

        //Velocity
        if (bot.getVelocityCalculator().isVelocityReplace()) {
            velocityTaken = true;
            Vector velocityKb = bot.getVelocityCalculator().getReplacedVelocity(motion.clone());
            bot.getVelocityCalculator().setVelocityReplace(false);

            double attackMultiplier = Math.max(bot.getMovements().getAttackMultiplier(),0.6);

            if (advancedReduce) if (bot.getMovements().isSprint()) velocityKb.multiply(new Vector(attackMultiplier,1,attackMultiplier));
            motion.setY(0);
            motion.add(velocityKb);
        }

        //Jumps
        if (bot.getMovements().isJump() && bot.getMovements().isOnGround()) {
            double jumpValue = 0.42 + bot.getEffectsManager().getLevel(PotionEffectType.JUMP) * 0.1;
            motion.setY(jumpValue);
            bot.getMovements().setJump(false);
        }

        sendMove(motion.clone(),!velocityTaken);
        updateValues(motion.clone(),true);
    }

    public static void sendMove(Vector motion,boolean addLastMotion) {
        bot.getMovements().move(motion,addLastMotion);
    }

    public void updateValues(Vector motion,boolean updateFriction) {
        Location loc = bot.getMovements().getLocation().toVector().subtract(new Vector(0,0.01,0)).toLocation(bot.getMovements().getLocation().getWorld());
        Material block = BlockManager.getBlockType(loc);

        bot.getMovements().setOnGround(onGround(loc));

        if (updateFriction) {
            boolean onGround = bot.getMovements().isOnGround();
            boolean lastOnGround = this.lastOnGround;
            this.lastOnGround = onGround;

            float friction = (CraftiFriction.getFactor(block)) * 0.91f;
            if (!lastOnGround) friction = 0.91f;

            bot.getMovements().setFriction(friction);
        }
    }

    public static boolean onGround(Location loc) {
        AABB playerBox = new AABB(new Vector(-0.29,0.0,-0.29),new Vector(0.29,1.8,0.29));
        World world = loc.getWorld();
        Location min = loc.toVector().add(playerBox.getMin()).toLocation(world);
        Location max = loc.toVector().add(playerBox.getMax().clone().setY(0)).toLocation(world);

        double var1 = (loc.getY() - loc.getBlockY());
        double var2 = var1 % (1.0 / 16.0);
        double var3 = var1 % (1.0 / 16.0) - 1.0 / 16;
        double var4 = Math.abs(Math.min(var2,var3));
        return RayTrace.blockCollidesCheck(min,max);
    }

    public static Vector getDirection(float yaw,float pitch) {
        Vector result = new Vector(0,0,0);
        double xz = Math.cos(Math.toRadians(pitch));
        result.setX(-xz * Math.sin(Math.toRadians(yaw)));
        result.setZ(xz * Math.cos(Math.toRadians(yaw)));

        return result;
    }
}
