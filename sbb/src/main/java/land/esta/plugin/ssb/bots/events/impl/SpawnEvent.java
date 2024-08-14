package land.esta.plugin.ssb.bots.events.impl;

import lombok.Getter;
import land.esta.plugin.ssb.bots.events.Event;
import land.esta.plugin.ssb.bots.Bot;

@Getter
public class SpawnEvent extends Event {

    public SpawnEvent(Bot bot) {
        super(bot);
    }
}
