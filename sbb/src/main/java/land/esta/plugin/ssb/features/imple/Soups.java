package land.esta.plugin.ssb.features.imple;

import land.esta.plugin.ssb.EstaRobotsPlugin;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.botevents.events.BotHealthChangeEvent;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.bots.mechanics.InventoryManager;
import land.esta.plugin.ssb.features.Feature;
import me.lucko.helper.Schedulers;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Soups extends Feature {

    private int soupTime = 5;
    private int soupHealth = 10;

    @Override
    public void listen(Event event) {
        if (event instanceof BotHealthChangeEvent) {
            BotHealthChangeEvent e = (BotHealthChangeEvent) event;
            if (e.getAfter() <= soupHealth) {
                Bot bot = e.getBot();
                InventoryManager manager = bot.getInventoryManager();
                List<ItemStack> soups = manager.hasItem(Material.MUSHROOM_SOUP);
                if (soups.size() <= 0) return;

                ItemStack soup = soups.get(0);
                int slot = manager.getItemSlot(soup);
                manager.setMainHand(slot);

                Schedulers
                        .sync()
                        .runLater(() -> {
                            if (!bot.isAlive()) return;
                            manager.setMainHand(1);
                            bot.getHealthManager().addHealth(6);
                            manager.removeItem(slot);
                        }, soupTime)
                        .bindWith(bot.getTerminable());
            }
        }
    }
}
