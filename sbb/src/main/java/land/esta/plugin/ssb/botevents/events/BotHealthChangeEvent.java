package land.esta.plugin.ssb.botevents.events;

import lombok.Getter;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.bots.Bot;

@Getter
public class BotHealthChangeEvent extends Event {

    private final double after;
    private final double before;

    public BotHealthChangeEvent(Bot bot, double after, double before) {
        super(bot);
        this.after = after;
        this.before = before;
    }
}
