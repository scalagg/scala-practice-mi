package land.esta.plugin.ssb.bots.events.impl;

import lombok.Getter;
import lombok.Setter;
import land.esta.plugin.ssb.bots.events.Event;
import land.esta.plugin.ssb.bots.Bot;
import net.minecraft.server.v1_8_R3.DamageSource;

import java.util.concurrent.atomic.AtomicInteger;


public class BotDamageEvent extends Event {
    DamageSource source;
    @Setter
    @Getter
    boolean canceled;

    public BotDamageEvent(Bot bot) {
        super(bot);
    }


    public BotDamageEvent(Bot bot,DamageSource source) {
        super(bot);
        AtomicInteger a = new AtomicInteger();
        this.source = source;
    }

    public DamageSource getSource() {
        return source;
    }

}
