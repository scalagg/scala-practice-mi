package land.esta.plugin.ssb.features.imple;

import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.features.Feature;
import net.minecraft.server.v1_8_R3.DamageSource;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffectType;

public class LiquidFeature extends Feature {
    @Override
    public void tickEvent(Bot bot) {
        Block block = bot.getMovements().getLocation().getBlock();
        Block above = bot.getMovements().getLocation().clone().add(0, 1, 0).getBlock();
        if (!block.isLiquid()) return;

        boolean lava = block.getType().equals(Material.LAVA) || block.getType().equals(Material.STATIONARY_LAVA);
        boolean water = block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER);

        if (!lava) {
            lava = above.getType().equals(Material.LAVA) || above.getType().equals(Material.STATIONARY_LAVA);
        }

        if (!water) {
            water = above.getType().equals(Material.WATER) || above.getType().equals(Material.STATIONARY_WATER);
        }

        if (lava) {
            int fireRess = bot.getEffectsManager().getLevel(PotionEffectType.FIRE_RESISTANCE);

            if (fireRess != 0) return;
            if (System.currentTimeMillis() - 1000 < bot.getLastLiquid()) return;

            bot.setLastLiquid(System.currentTimeMillis());

            bot.getDamageManager().playSound();
            bot.getDamageManager().damageEffect();
            bot.getHealthManager().removeHealth(2);
            bot.getHealthManager().fire(300);
        }

        if (water) {
            bot.getPacketsManager().setPlayerOnFirePackets(false);
            bot.getHealthManager().setFireTicks(0);
        }
    }
}
