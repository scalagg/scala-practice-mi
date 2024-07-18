package land.esta.plugin.ssb.botevents.events;

import lombok.Getter;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.bots.Bot;

@Getter
public class BotArmorBreakEvent extends Event {
    private final int armorSlot;

    public BotArmorBreakEvent(Bot bot, int armorSlot) {
        super(bot);
        this.armorSlot = armorSlot;
    }
}
