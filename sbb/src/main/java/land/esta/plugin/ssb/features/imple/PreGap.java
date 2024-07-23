package land.esta.plugin.ssb.features.imple;

import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.botevents.events.SpawnEvent;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.features.Feature;
import land.esta.plugin.ssb.EstaRobotsPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PreGap extends Feature {

    @Override
    public void listen(Event event) {
        if (event instanceof SpawnEvent) {
            Bot bot = event.bot;
            //Run a tick later so the practice plugin has time to provide you with the items

            new BukkitRunnable() {
                @Override
                public void run() {
                    boolean a = bot.getInventoryManager().hasItem(322).size() > 0;
                    boolean b = bot.getProfile().value(RootAccessor.INSTANCE.getEatingSettings().getPreGap());

                    //Bukkit.broadcastMessage("a " + a + " b " + b);
                    if (a && b) {
                        //Bukkit.broadcastMessage("pre Gap");
                        EatingAndDrinking.eatGap(bot);
                    }
                }
            }.runTaskLaterAsynchronously(EstaRobotsPlugin.getInstance(),20 * 6);
        }
    }
}
