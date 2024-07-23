package land.esta.plugin.ssb.npc;

import com.mojang.authlib.GameProfile;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.EstaRobotsPlugin;
import land.esta.plugin.ssb.utils.Nms;
import net.minecraft.server.v1_8_R3.*;

import java.lang.ref.WeakReference;

public class NPCEntity extends EntityPlayer {

    public NPCEntity(MinecraftServer minecraftserver, WeakReference<WorldServer> worldserver, GameProfile gameprofile, PlayerInteractManager playerinteractmanager) {
        super(minecraftserver, worldserver.get(), gameprofile, playerinteractmanager);
    }

    @Override
    protected boolean d(DamageSource damagesource, float f) {
        boolean d = super.d(damagesource, f);
        Bot bot = EstaRobotsPlugin.getInstance().getBotManager().getNpcFromId(getId());
        if (bot == null) return d;

        bot.getDamageManager().damage(damagesource,f);

        int fireLevel = EnchantmentManager.getFireAspectEnchantmentLevel(Nms.getPlayer(bot.getMovements().getTargetLocate().getTargetEntity()));

        if (fireLevel > 0) {
            bot.getHealthManager().fire(fireLevel * 4 * 20);
        }

        return d;
    }

}
