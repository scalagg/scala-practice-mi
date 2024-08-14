package land.esta.plugin.ssb.features.imple;

import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import land.esta.plugin.ssb.bots.events.Event;
import land.esta.plugin.ssb.bots.events.impl.SpawnEvent;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.features.Feature;
import me.lucko.helper.Schedulers;

public class PreGap extends Feature {

    @Override
    public void listen(Event event) {
        if (event instanceof SpawnEvent) {
            Bot bot = event.getBot();
            //Run a tick later so the practice plugin has time to provide you with the items

            Schedulers
                    .async()
                    .runLater(() -> {
                        boolean a = !bot.getInventoryManager().hasItem(322).isEmpty();
                        boolean b = bot.getProfile().value(RootAccessor.INSTANCE.getEatingSettings().getPreGap());

                        //Bukkit.broadcastMessage("a " + a + " b " + b);
                        if (a && b) {
                            //Bukkit.broadcastMessage("pre Gap");
                            EatingAndDrinking.eatGap(bot);
                        }
                    }, 20 * 6)
                    .bindWith(bot.getTerminable());
        }
    }
}
