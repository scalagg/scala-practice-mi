package land.esta.plugin.ssb.utils.gui;

import org.bukkit.entity.Player;

public class GlobalGui extends Gui {

    private Runnable update;

    public GlobalGui(String title, int size) {
        super(title, size);
    }

    public void openGui(Player player) {
        player.closeInventory();
        update.run();
        openInventory(player);
    }

    public void minOpen(Player player, int... temp) {
        update.run();
        openInventory(player);
    }

    public void setGui(Runnable update) {
        this.update = update;
    }
}
