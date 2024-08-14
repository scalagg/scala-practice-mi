package land.esta.plugin.ssb.bots.events.impl;

import land.esta.plugin.ssb.bots.events.Event;
import land.esta.plugin.ssb.bots.Bot;

public class BotRequiredToEatEvent extends Event {

    public BotRequiredToEatEvent(Bot bot) {
        super(bot);
    }
}
