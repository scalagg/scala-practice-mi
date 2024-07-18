package land.esta.plugin.ssb.botevents.events;

import lombok.Getter;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.bots.Bot;
import org.bukkit.potion.PotionEffectType;

@Getter
public class EffectEndEvent extends Event {

    private final PotionEffectType type;

    public EffectEndEvent(Bot bot,PotionEffectType type) {
        super(bot);
        this.type = type;
    }
}
