package gg.solara.plugin.robots.npc;

import com.mojang.authlib.GameProfile;
import gg.solara.plugin.robots.bots.Bot;
import gg.solara.plugin.robots.SolaraRobotsPlugin;
import gg.solara.plugin.robots.utils.Nms;
import net.minecraft.server.v1_8_R3.*;

import java.lang.ref.WeakReference;

public class NPCEntity extends EntityPlayer {

    private final WeakReference<Bot> bot;

    public NPCEntity(Bot bot, MinecraftServer minecraftserver, WorldServer worldserver, GameProfile gameprofile, PlayerInteractManager playerinteractmanager) {
        super(minecraftserver, worldserver, gameprofile, playerinteractmanager);
        this.bot = new WeakReference<>(bot);
    }

    @Override
    protected boolean d(DamageSource damagesource, float f) {
        var botInstance = bot.get();
        if (botInstance == null) {
            return super.d(damagesource, f);
        }
        boolean damage = super.d(damagesource, f);
        botInstance.getDamageManager().damage(damagesource,f);

        int fireLevel = EnchantmentManager.getFireAspectEnchantmentLevel(
                Nms.getPlayer(botInstance.getMovements().getTargetLocate().getTargetEntity())
        );

        if (fireLevel > 0) {
            botInstance.getHealthManager().fire(fireLevel * 4 * 20);
        }

        return damage;
    }

}
