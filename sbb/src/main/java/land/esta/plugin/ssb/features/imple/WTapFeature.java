package land.esta.plugin.ssb.features.imple;

import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.botevents.events.BotAttackLandEvent;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.features.Feature;
import land.esta.plugin.ssb.EstaRobotsPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class WTapFeature extends Feature {

    @Override
    public void listen(Event event) {
        if (event instanceof BotAttackLandEvent) {
            BotAttackLandEvent e = (BotAttackLandEvent) event;
            Bot bot = e.bot;
            int random = (ThreadLocalRandom.current().nextInt(100));

            bot.getMovements().setAttackSprint(false);
            if (!bot.getProfile().value(RootAccessor.INSTANCE.getWTapSettings().getEnabled())) return;
            if (random > bot.getProfile().value(RootAccessor.INSTANCE.getWTapSettings().getChance())) return;

            final long tapLong;
            if (EstaRobotsPlugin.getInstance().getDataManager().getPlayer(e.getPlayer()).getBotCombo() > 3) {
                tapLong = bot.getProfile().value(RootAccessor.INSTANCE.getWTapSettings().getComboTapLong());
            } else {
                tapLong = bot.getProfile().value(RootAccessor.INSTANCE.getWTapSettings().getComboTapLong());
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!bot.isAlive()) return;


                    if (bot.getProfile().value(RootAccessor.INSTANCE.getWTapSettings().getBlockingHitting())) {
                        //Block hitting handling
                        bot.getPacketsManager().sendBlockHittingPackets(true);
                        bot.getMovements().setUse(true);
                    } else {
                        //W tap handing
                        bot.getMovements().setMove(false);
                    }
                    bot.getMovements().setWtap(true);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!bot.isAlive()) return;

                            if (bot.getProfile().value(RootAccessor.INSTANCE.getWTapSettings().getBlockingHitting())) {
                                //Block hitting handling
                                bot.getPacketsManager().sendBlockHittingPackets(false);
                                bot.getMovements().setUse(false);
                            } else {
                                //W tap handing
                                bot.getMovements().setMove(true);
                            }

                            bot.getMovements().setWtap(false);
                        }
                    }.runTaskLater(EstaRobotsPlugin.getInstance(),tapLong);
                }
            }.runTaskLater(EstaRobotsPlugin.getInstance(),bot.getProfile().value(RootAccessor.INSTANCE.getWTapSettings().getTapStart()));
        }
    }
}
