package land.esta.plugin.ssb.bots.events.impl;

import lombok.Getter;
import land.esta.plugin.ssb.bots.events.Event;
import land.esta.plugin.ssb.bots.Bot;
import org.bukkit.entity.Player;

@Getter
public class BotAttackLandEvent extends Event {
    private final Player player;

    public BotAttackLandEvent(Bot bot, Player player) {
        super(bot);
        this.player = player;
    }
}
