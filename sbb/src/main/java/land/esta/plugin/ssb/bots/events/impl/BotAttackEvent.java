package land.esta.plugin.ssb.bots.events.impl;

import land.esta.plugin.ssb.bots.events.Event;
import land.esta.plugin.ssb.bots.Bot;

public class BotAttackEvent extends Event {

    public BotAttackEvent(Bot bot) {
        super(bot);
    }
}
