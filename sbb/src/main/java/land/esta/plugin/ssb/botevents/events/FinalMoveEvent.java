package land.esta.plugin.ssb.botevents.events;

import lombok.Getter;
import lombok.Setter;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.bots.Bot;
import org.bukkit.Location;

@Getter
@Setter
public class FinalMoveEvent extends Event {

    private Location to;
    private Location from;

    public FinalMoveEvent(Bot bot, Location to, Location from) {
        super(bot);
        this.to = to;
        this.from = from;
    }
}
