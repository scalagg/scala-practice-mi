package gg.solara.plugin.robots.features.impl;

import gg.solara.plugin.robots.bot.behavior.ExecutionBehavior;
import gg.solara.plugin.robots.bot.behavior.actions.GoldenAppleConsumeAction;
import gg.solara.plugin.robots.bot.setting.setting.RootAccessor;
import gg.solara.plugin.robots.bots.events.Event;
import gg.solara.plugin.robots.bots.events.impl.SpawnEvent;
import gg.solara.plugin.robots.bots.Bot;
import gg.solara.plugin.robots.features.Feature;
import gg.tropic.practice.games.event.GameStartEvent;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;

import java.util.concurrent.TimeUnit;

public class PreGap extends Feature {

    @Override
    public void listen(Event event) {
        if (event instanceof SpawnEvent) {
            var bot = event.getBot();
            var shouldPreGap = bot.getProfile().value(RootAccessor.INSTANCE.getEatingSettings().getPreGap());
            if (!shouldPreGap) {
                return;
            }

            Events
                    .subscribe(GameStartEvent.class)
                    .filter(startEvent -> startEvent.getGame().getExpectation().equals(bot.getGame().getExpectation()))
                    .handler(startEvent -> {
                        Schedulers
                                .sync()
                                .runLater(() -> {
                                    var hasGoldenApple = !bot.getInventoryManager().hasItem(322).isEmpty();
                                    if (!hasGoldenApple) {
                                        return;
                                    }

                                    bot.getActionController().runAction(
                                            GoldenAppleConsumeAction.INSTANCE,
                                            (configuration) -> {
                                                configuration.setExecutionBehavior(ExecutionBehavior.Override);
                                                configuration.setShouldContinueMovement(false);
                                                return null;
                                            }
                                    );
                                }, 1L, TimeUnit.SECONDS)
                                .bindWith(bot.getTerminable());
                    })
                    .bindWith(bot.getTerminable());
        }
    }
}
