package land.esta.plugin.ssb.botevents.events;

import lombok.Getter;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.bots.Bot;
import org.bukkit.inventory.ItemStack;

//Also being called when finished drinking.
@Getter
public class FinishEatingEvent extends Event {

    private final ItemStack item;

    public FinishEatingEvent(Bot bot,ItemStack item) {
        super(bot);
        this.item = item;
    }
}
