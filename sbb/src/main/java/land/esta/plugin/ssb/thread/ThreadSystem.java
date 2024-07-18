package land.esta.plugin.ssb.thread;

import lombok.Getter;
import land.esta.plugin.ssb.bots.Bot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class ThreadSystem {

    private final List<BotsThread> threads;
    private final ExecutorService botsManagerThread;
    private final ExecutorService guiThread;
    private final ExecutorService botAttacksThread;

    public ThreadSystem() {
        threads = new ArrayList<>();
        botsManagerThread = Executors.newSingleThreadExecutor();
        guiThread = Executors.newSingleThreadExecutor();
        botAttacksThread = Executors.newSingleThreadExecutor();
    }

    public ExecutorService findThread(Bot bot) {
        // Uses an already created thread
        for (BotsThread bThread : threads) {
            ExecutorService thread = bThread.join(bot);
            if (thread != null) return thread;
        }

        // Creates a new thread
        BotsThread bThread = new BotsThread();
        threads.add(bThread);
        return bThread.join(bot);
    }

    public void quit(Bot bot) {
        for (BotsThread bThread : threads) {
            bThread.quit(bot);
        }
    }
}
