package land.esta.plugin.ssb.features.imple;

import land.esta.plugin.ssb.SmartBotsBackend;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.botevents.events.BotArmorBreakEvent;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.features.Feature;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ArmorEquipFeature extends Feature {

    @Override
    public void listen(Event event) {
        if (event instanceof BotArmorBreakEvent) {
            BotArmorBreakEvent e = (BotArmorBreakEvent) event;

            int index = e.getArmorSlot();
            Bot bot = e.bot;

            // 1 second delay
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!bot.isAlive()) return;
                    List<ItemStack> stack = bot.getInventoryManager().hasArmor(index);

                    if (stack.isEmpty()) return;
                    bot.getInventoryManager().updateArmorContents(stack.get(0), index);
                    bot.getInventoryManager().removeItem(bot.getInventoryManager().getItemSlot(stack.get(0)));
                }
            }.runTaskLater(SmartBotsBackend.getInstance(),20);
        }
    }
}
