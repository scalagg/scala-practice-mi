package gg.solara.plugin.robots.bots.events;

import gg.solara.plugin.robots.bots.Bot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class Event extends org.bukkit.event.Event {
    public static final HandlerList HANDLER_LIST = new HandlerList();

    private final Bot bot;

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
