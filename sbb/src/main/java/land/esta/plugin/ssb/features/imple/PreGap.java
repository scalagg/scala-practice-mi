package land.esta.plugin.ssb.features.imple;

import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.botevents.events.SpawnEvent;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.features.Feature;
import land.esta.plugin.ssb.SmartBotsBackend;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

public class PreGap extends Feature {
    private static final

    @Override
    public void listen(Event event) {
        if (event instanceof SpawnEvent) {
            Bot bot = event.bot;
            //Run a tick later so the practice plugin has time to provide you with the items

            new BukkitRunnable() {
                @Override
                public void run() {
                    boolean botHasGoldenApple = !bot.getInventoryManager().hasItem(Material.GOLDEN_APPLE).isEmpty();
                    boolean botCanPreGap = bot.getSettingManager().getEatingSettings().getPreGap().isBooleanValue();

                    //Bukkit.broadcastMessage("botHasGoldenApple " + botHasGoldenApple + " botCanPreGap " + botCanPreGap);
                    if (botHasGoldenApple && botCanPreGap) {
                        //Bukkit.broadcastMessage("pre Gap");
                        EatingAndDrinking.eatGap(bot);
                    }
                }
            }.runTaskLaterAsynchronously(SmartBotsBackend.getInstance(),20 * 6);
        }
    }
}
