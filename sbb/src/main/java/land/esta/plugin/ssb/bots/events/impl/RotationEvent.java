package land.esta.plugin.ssb.bots.events.impl;

import lombok.Getter;
import lombok.Setter;
import land.esta.plugin.ssb.bots.events.Event;
import land.esta.plugin.ssb.bots.Bot;

@Getter
@Setter
public class RotationEvent extends Event {

    private float newYaw;
    private float newPitch;

    private float oldYaw;
    private float oldPitch;

    public RotationEvent(Bot bot, float newYaw,float newPitch,float oldYaw, float oldPitch) {
        super(bot);
        this.newYaw = newYaw;
        this.newPitch = newPitch;
        this.oldPitch = oldPitch;
        this.oldYaw = oldYaw;
    }
}
