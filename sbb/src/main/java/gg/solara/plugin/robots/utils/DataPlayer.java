package gg.solara.plugin.robots.utils;

import gg.solara.plugin.robots.SolaraRobotsPlugin;
import gg.solara.plugin.robots.bot.setting.setting.RootAccessor;
import lombok.Data;
import gg.solara.plugin.robots.bots.Bot;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataPlayer {
    private final Player player;
    private Vector motion;

    private final List<Vector> locations = new ArrayList<>();

    public void handleFlying(PacketPlayInFlying wrapper) {
        Vector posUpdate = new Vector(wrapper.a(),wrapper.b(),wrapper.c());
        handlePos(posUpdate);
    }

    private int hitCounter;
    private int combo;
    private int botHitCounter;
    private int botCombo;
    private long timeOutChat;
    private boolean saving = false;
    private boolean updatingGui = false;

    public void botHit() {
        botHitCounter++;
        botCombo++;
        combo = 0;
    }

    public void hit() {
        hitCounter++;
        combo++;
        botCombo = 0;
    }

    public boolean handle() {
        Bot bot = SolaraRobotsPlugin.getInstance().getBotManager().getNpcFromTarget(player);
        if (bot == null) return false;

        long diff = System.currentTimeMillis() - bot.getAttackingManager().getAttacked();
        bot.getAttackingManager().setLandAttack(diff < 150);
        return (diff < 150 && !bot.getDamageManager().isCombo()) || bot.getAttackingManager().isProjectile();
    }

    public Vector velocity() {
        Bot bot = SolaraRobotsPlugin.getInstance().getBotManager().getNpcFromTarget(player);
        if (bot == null) return null;

        Vector dir = MathUtils.getDirection(bot.getMovements().getLocation().getYaw());
        if (bot.getAttackingManager().isProjectile()) return dir.multiply(0.8).setY(0.4);
        double h = 0.47;
        double v = 0.35;
        double reach = 3.5;
        double distMultiplier = 0.15;

        var wtapEnabled = bot.getProfile().value(
                RootAccessor.INSTANCE.getWTapSettings().getEnabled()
        );

        if (bot.getMovements().isAttackSprint() || !wtapEnabled) h *= 1.2;
        if (player.isOnGround()) h *= 1.1;
        double extraH = Math.min(Math.max((reach - (bot.getMovements().getLocation().distance(player.getLocation()) - 0.3)) * distMultiplier,0),0.4);
        h += extraH;
        double speed = 1;
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if (potionEffect.getType().equals(PotionEffectType.SPEED)) speed = potionEffect.getAmplifier() * 0.2 + 1;
        }
        h += 0.1 * 1.3 * speed;

        dir.multiply(h);
        dir.setY(v);

        return dir;
    }

    public void handlePos(Vector pos) {
        if (pos.equals(new Vector(0,0,0))) return;
        locations.add(pos);

        if (locations.size() > 3) locations.remove(0);
    }

    public Vector getLastLoc() {
        if (locations.isEmpty()) return new Vector(0,0,0);
        return locations.get(locations.size() - 1);
    }

    @EventHandler
    public void damage(EntityDamageByEntityEvent e) {
        if (e.getEntity().getEntityId() == player.getEntityId()) {
            Bot bot = SolaraRobotsPlugin.getInstance().getBotManager().getNpcFromTarget(player);
            if (bot != null) bot.getAttackingManager().setAttackTicks(0);
        }
    }
}
