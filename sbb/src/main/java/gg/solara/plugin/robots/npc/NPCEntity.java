package gg.solara.plugin.robots.npc;

import com.mojang.authlib.GameProfile;
import gg.solara.plugin.robots.bots.Bot;
import gg.solara.plugin.robots.SolaraRobotsPlugin;
import gg.solara.plugin.robots.utils.Nms;
import net.minecraft.server.v1_8_R3.*;

public class NPCEntity extends EntityPlayer {

    public NPCEntity(MinecraftServer minecraftserver, WorldServer worldserver, GameProfile gameprofile, PlayerInteractManager playerinteractmanager) {
        super(minecraftserver, worldserver, gameprofile, playerinteractmanager);
    }

    @Override
    protected boolean d(DamageSource damagesource, float f) {
        boolean d = super.d(damagesource, f);
        Bot bot = SolaraRobotsPlugin.getInstance().getBotManager().getNpcFromId(getId());
        if (bot == null) return d;

        bot.getDamageManager().damage(damagesource,f);

        int fireLevel = EnchantmentManager.getFireAspectEnchantmentLevel(Nms.getPlayer(bot.getMovements().getTargetLocate().getTargetEntity()));

        if (fireLevel > 0) {
            bot.getHealthManager().fire(fireLevel * 4 * 20);
        }

        return d;
    }

}
