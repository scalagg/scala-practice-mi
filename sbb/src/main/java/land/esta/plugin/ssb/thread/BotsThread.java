package land.esta.plugin.ssb.thread;

import land.esta.plugin.ssb.bots.Bot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BotsThread {

    private final ExecutorService thread;
    private final List<Bot> bots;

    public BotsThread() {
        thread = Executors.newSingleThreadExecutor();
        bots = new ArrayList<>();
    }

    public ExecutorService join(Bot bot) {
        if (bots.size() >= 5) return null;

        bots.add(bot);
        return thread;
    }

    public void quit(Bot bot) {
        bots.remove(bot);
    }
}
