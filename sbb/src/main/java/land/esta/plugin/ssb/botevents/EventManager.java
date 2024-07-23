package land.esta.plugin.ssb.botevents;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class EventManager {

    private final ArrayList<BotEvent> events = new ArrayList<>();

    public void registerEvent(BotEvent event) {
        events.add(event);
    }

    public void unregisterEvent(BotEvent event) {
        events.remove(event);
    }
}
