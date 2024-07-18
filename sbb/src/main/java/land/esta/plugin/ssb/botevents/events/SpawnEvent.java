package land.esta.plugin.ssb.botevents.events;

import lombok.Getter;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.bots.Bot;

@Getter
public class SpawnEvent extends Event {

    public SpawnEvent(Bot bot) {
        super(bot);
    }
}
