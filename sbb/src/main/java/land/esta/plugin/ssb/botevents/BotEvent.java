package land.esta.plugin.ssb.botevents;

import land.esta.plugin.ssb.bots.Bot;

public class BotEvent {

    public void listen(Event event) {}

    public void tickEvent(Bot bot) {}

    public void call(Event event) {
        listen(event);
    }

    public void execute(Bot bot) {
        tickEvent(bot);
    }
}
