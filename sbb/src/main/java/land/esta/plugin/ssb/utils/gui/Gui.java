package land.esta.plugin.ssb.utils.gui;

import land.esta.plugin.ssb.SmartBotsBackend;
import lombok.Getter;
import lombok.Setter;
import land.esta.plugin.ssb.utils.Color;
import land.esta.plugin.ssb.utils.objects.PlayerRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Gui implements Listener {
    private final Inventory gui;
    private final int size;
    private HashMap<ItemStack,PlayerRunnable> clickAction;
    private Map<String,PlayerRunnable> nameMap;
    private Map<String,PlayerRunnable> nameMap2;
    private HashMap<ItemStack,PlayerRunnable> rightClickAction;
    private HashMap<ItemStack,PlayerRunnable> shiftAction = new HashMap<>();
    private HashMap<ItemStack,PlayerRunnable> shiftRightAction = new HashMap<>();
    private HashMap<ItemStack, PlayerRunnable> middleAction;
    private String title;

    public Gui(String title,int size) {
        gui = Bukkit.createInventory(null, size, Color.code(title));
        this.size = size;
        this.title = title;

        clickAction = new HashMap<>();
        rightClickAction = new HashMap<>();
        nameMap = new HashMap<>();
        nameMap2 = new HashMap<>();
        middleAction = new HashMap<>();
        Bukkit.getServer().getPluginManager().registerEvents(this, SmartBotsBackend.getInstance());
    }

    public void reset() {
        clickAction = new HashMap<>();
        gui.clear();
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null || !e.getClickedInventory().equals(gui)) return;
        e.setCancelled(true);

        ItemStack stack = e.getCurrentItem();
        if (stack == null || stack.getItemMeta() == null) return;

        PlayerRunnable runnable = null;

        if (e.getClick().equals(ClickType.MIDDLE)) {
            runnable = middleAction.get(stack);
        } else {
            boolean isLeftClick = e.isLeftClick();
            boolean isShiftClick = e.isShiftClick();
            Map<String, PlayerRunnable> nameMapToUse = isLeftClick ? nameMap : nameMap2;

            if (isShiftClick) {
                Map<ItemStack, PlayerRunnable> shiftActionMap = isLeftClick ? shiftAction : shiftRightAction;
                runnable = shiftActionMap.getOrDefault(stack, null);
            }

            if (runnable == null) {
                runnable = (isLeftClick ? clickAction : rightClickAction).getOrDefault(stack, null);
            }

            if (runnable == null && stack.getItemMeta().getDisplayName() != null) {
                String name = stack.getItemMeta().getDisplayName();
                runnable = nameMapToUse.getOrDefault(name, null);
            }
        }

        if (runnable != null) {
            runnable.run(player, e.getSlot());
        }
    }

    public void openInventory(final HumanEntity ent) {
        ent.openInventory(gui);
    }

    public void createGuiItem(final Material material, int slot, final String name,String color, PlayerRunnable action, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(Color.code(color + name));

        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(Color.code(s));
        }

        // Set the lore of the item
        meta.setLore((newLore));

        item.setItemMeta(meta);

        clickAction.put(item,action);

        gui.setItem(slot,item);
    }

    public void createGuiItem(final Material material, int slot, final String name,String color, PlayerRunnable action, final List<String > lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(Color.code(color + name));

        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(Color.code(s));
        }

        // Set the lore of the item
        meta.setLore((newLore));

        item.setItemMeta(meta);

        clickAction.put(item,action);

        gui.setItem(slot,item);
    }

    public void createGuiItem(final ItemStack stack,int slot, final String name, PlayerRunnable action, final List<String> lore,boolean enchanted) {
        final ItemStack item = new ItemStack(stack.getType(), stack.getAmount());
        if (enchanted) item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(Color.code(name));

        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(Color.code(s));
        }

        // Set the lore of the item
        meta.setLore((newLore));

        if (enchanted) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);

        clickAction.put(item,action);

        gui.setItem(slot, item);
    }

    public void createGuiItem(final ItemStack stack,int slot, final String name, PlayerRunnable action,PlayerRunnable rightAction,PlayerRunnable shiftAction,PlayerRunnable shiftRightAction, final List<String> lore) {
        final ItemStack item = new ItemStack(stack.getType(), stack.getAmount());
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(Color.code(name));

        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(Color.code(s));
        }

        // Set the lore of the item
        meta.setLore((newLore));

        item.setItemMeta(meta);

        clickAction.put(item,action);
        rightClickAction.put(item,rightAction);
        this.shiftAction.put(item,shiftAction);
        this.shiftRightAction.put(item,shiftRightAction);

        gui.setItem(slot,item);
    }

    public void createGuiItem(final ItemStack stack, int slot, final String name, PlayerRunnable action, PlayerRunnable rightAction, PlayerRunnable shiftAction, PlayerRunnable shiftRightAction, PlayerRunnable middleAction, final List<String> lore) {
        final ItemStack item = new ItemStack(stack.getType(), stack.getAmount());
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(Color.code(name));

        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(Color.code(s));
        }

        // Set the lore of the item
        meta.setLore((newLore));

        item.setItemMeta(meta);

        clickAction.put(item, action);
        rightClickAction.put(item, rightAction);
        this.shiftAction.put(item, shiftAction);
        this.shiftRightAction.put(item, shiftRightAction);
        this.middleAction.put(item, middleAction);

        gui.setItem(slot, item);
    }

    public void createGuiItem(final ItemStack stack,int slot, final String name, PlayerRunnable action,PlayerRunnable rightAction, final List<String> lore) {
        final ItemStack item = new ItemStack(stack.getType(), stack.getAmount());
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(Color.code(name));

        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(Color.code(s));
        }

        // Set the lore of the item
        meta.setLore((newLore));

        item.setItemMeta(meta);

        clickAction.put(item, action);
        rightClickAction.put(item, rightAction);

        gui.setItem(slot, item);
    }

    public void createGuiItemUsingName(final ItemStack item,int slot, final String name, PlayerRunnable action,PlayerRunnable rightAction, final List<String> lore) {
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(Color.code(name));

        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(Color.code(s));
        }

        // Set the lore of the item
        if (newLore.size() > 0) meta.setLore((newLore));

        item.setItemMeta(meta);

        gui.setItem(slot,item);

        nameMap.put(item.getItemMeta().getDisplayName(),action);
        nameMap2.put(item.getItemMeta().getDisplayName(),rightAction);
    }

    public void createGuiItem(final ItemStack item,int slot, final String name, PlayerRunnable action, final String... lore) {
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(Color.code(name));

        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(Color.code(s));
        }

        // Set the lore of the item
        if (newLore.size() > 0) meta.setLore((newLore));

        item.setItemMeta(meta);

        gui.setItem(slot,item);

        clickAction.put(item,action);
    }

    public void createGuiItemUsingName(final ItemStack item,int slot, final String name, PlayerRunnable action, final String... lore) {
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(Color.code(name));

        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(Color.code(s));
        }

        // Set the lore of the item
        if (newLore.size() > 0) meta.setLore((newLore));

        item.setItemMeta(meta);

        gui.setItem(slot,item);

        nameMap.put(item.getItemMeta().getDisplayName(),action);
    }

    public void createGuiItem(final Material material, int amount, Byte id, int slot, final String name, PlayerRunnable action, final String... lore) {
        final ItemStack item = new ItemStack(material, amount,id);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(Color.code(name));

        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(Color.code(s));
        }

        // Set the lore of the item
        meta.setLore((newLore));

        item.setItemMeta(meta);

        clickAction.put(item,action);

        gui.setItem(slot,item);
    }

    public void createGuiItem(final ItemStack item, int slot) {
        gui.setItem(slot,item);
    }

    public void setBackGroundColorSlot(int color,int slot) {
        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1,(short) color);

        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(Color.code("&b"));
        glass.setItemMeta(glassMeta);

        gui.setItem(slot, glass);
    }

    public void setBackGroundColor(int color) {
        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1,(short) color);

        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(Color.code("&b"));
        glass.setItemMeta(glassMeta);

        for(int i = 0; i<size;i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i,glass);
            }
            if (gui.getItem(i) != null) {
                if (gui.getItem(i).getType() == Material.AIR) {
                    gui.setItem(i, glass);
                }
            }
        }
    }
}
