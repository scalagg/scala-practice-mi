package gg.solara.plugin.robots.bots.events.impl;

import gg.solara.plugin.robots.bots.events.Event;
import gg.solara.plugin.robots.bots.Bot;

public class BotAttackEvent extends Event {

    public BotAttackEvent(Bot bot) {
        super(bot);
    }
}
