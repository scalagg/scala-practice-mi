package land.esta.plugin.ssb.bots.events.impl;

import lombok.Getter;
import land.esta.plugin.ssb.bots.events.Event;
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
