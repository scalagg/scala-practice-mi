package gg.solara.plugin.robots.bots.events.impl;

import lombok.Getter;
import gg.solara.plugin.robots.bots.events.Event;
import gg.solara.plugin.robots.bots.Bot;

@Getter
public class BotArmorBreakEvent extends Event {
    private final int armorSlot;

    public BotArmorBreakEvent(Bot bot, int armorSlot) {
        super(bot);
        this.armorSlot = armorSlot;
    }
}
