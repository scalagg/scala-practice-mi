package gg.solara.plugin.robots.bots.events.impl;

import gg.solara.plugin.robots.bots.events.Event;
import gg.solara.plugin.robots.bots.Bot;

public class BotRequiredToEatEvent extends Event {

    public BotRequiredToEatEvent(Bot bot) {
        super(bot);
    }
}
