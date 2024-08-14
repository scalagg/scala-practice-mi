package gg.solara.plugin.robots.bots.events.impl;

import lombok.Getter;
import gg.solara.plugin.robots.bots.events.Event;
import gg.solara.plugin.robots.bots.Bot;

@Getter
public class SpawnEvent extends Event {

    public SpawnEvent(Bot bot) {
        super(bot);
    }
}
