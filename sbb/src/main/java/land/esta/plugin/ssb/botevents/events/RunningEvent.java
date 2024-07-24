package land.esta.plugin.ssb.botevents.events;

import lombok.Getter;
import lombok.Setter;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.bots.Bot;

@Getter
@Setter
public class RunningEvent extends Event {

    private boolean forceEnd;
    private boolean shouldPause;
    private int ticks;

    public RunningEvent(Bot bot,int ticks) {
        super(bot);
        this.ticks = ticks;
    }
}
