package land.esta.plugin.ssb.chat;

public interface ChatRunnable {

    public boolean run(String typed); //The typed is what the player has typed in chat.
    // return true if you wish to cancel the event
}
