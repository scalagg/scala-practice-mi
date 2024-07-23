package land.esta.plugin.ssb.bots.mechanics;

import lombok.Getter;
import lombok.Setter;
import land.esta.plugin.ssb.botevents.events.BotHealthChangeEvent;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.bots.events.Manager;
import org.bukkit.potion.PotionEffectType;

@Getter
@Setter
public class HealthManager extends Manager {
    public HealthManager(Bot bot) {
        super(bot);
        health = maxHeath;
    }

    private double health;
    private int maxHeath = 20;
    private int naturalRegTicks;
    private int requiredNaturalRegTicks = 20;
    private float absHearts;
    private int regenTicks;
    private int fireTicks = 0;
    private boolean onFireUpdate = false;
    private boolean triggerHealthChangeEvent = false;
    private double healthChangeBefore;
    private double healthChangeAfter;

    @Override
    public void tickPre() {
        if (!getBot().getNpc().isAlive()) {
            getBot().getNpc().dead = false;
            deathEvent();
            return;
        }
        getBot().getNpc().setHealth(Math.min((float) health, 1));
        if (health <= 0 && !getBot().isAlive()) getBot().getNpc().setHealth((float) health);

        int regenLevel = getBot().getEffectsManager().getLevel(PotionEffectType.REGENERATION);

        //Effects
        regenerationCheck(regenLevel);
        absCheck();
        fireCheck();

        if (triggerHealthChangeEvent) {
            healthChangeEvent(healthChangeBefore, healthChangeAfter);
            triggerHealthChangeEvent = false;
        }
    }

    // Checks if the bot has abs effect
    public void absCheck() {
        if (getBot().getEffectsManager().getLevel(PotionEffectType.ABSORPTION) == 0) absHearts = 0;
    }

    //Regen Effect
    public void regenerationCheck(int level) {
        if (level == 0) {
            regenTicks = 0;
            return;
        }

        int requireTicks = 50;
        if (level == 2) {
            requireTicks = 25;
        } else if (level == 3) {
            requireTicks = 12;
        } else if (level == 4) {
            requireTicks = 6;
        } else if (level == 5) {
            requireTicks = 3;
        } else if (level >= 6) {
            requireTicks = 1;
        }

        if (regenTicks++ != 0 && regenTicks % requireTicks == 0)
            addHealth(1);
    }

    //Fire check
    public void fireCheck() {
        int fireRess = getBot().getEffectsManager().getLevel(PotionEffectType.FIRE_RESISTANCE);
        fireTicks = Math.max(0, fireTicks - 1);
        if (fireTicks == 0 && onFireUpdate) getBot().getPacketsManager().setPlayerOnFirePackets(false);

        if (fireRess >= 1 || fireTicks == 0) return;
        if (fireTicks % 10 == 0) removeHealth(1);
    }

    public void removeHealth(double amount) {
        healthChangeEvent(health, health - amount);
        health -= amount;
        if (health < 0) {
            deathEvent();
            //Bukkit.broadcastMessage("d1");
        }
    }

    public void deathEvent() {
        getBot().kill();
    }

    public void healthChangeEvent(double before, double after) {
        callEventAsync(new BotHealthChangeEvent(getBot(), after, before));
    }

    public void addHealth(double amount) {
        healthChangeBefore = health;
        health = Math.min(maxHeath, health + amount);
        healthChangeAfter = health;
        triggerHealthChangeEvent = true;
    }

    public void fire(int ticks) {
        getBot().getPacketsManager().setPlayerOnFirePackets(true);
        fireTicks = ticks;
        onFireUpdate = true;
    }
}
