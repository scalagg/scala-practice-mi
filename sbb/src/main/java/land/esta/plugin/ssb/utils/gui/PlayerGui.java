package land.esta.plugin.ssb.utils.gui;

import lombok.Getter;
import land.esta.plugin.ssb.data.DataPlayer;

@Getter
public class PlayerGui extends Gui {

    private Runnable update;
    private final DataPlayer data;

    public PlayerGui(String title, int size, DataPlayer data) {
        super(title, size);
        this.data = data;
    }

    public void openGui() {
        data.getPlayer().closeInventory();
        update.run();
        openInventory(data.getPlayer());
    }

    public void minOpen() {
        update.run();
        openInventory(data.getPlayer());
    }

    public void setGui(Runnable update) {
        this.update = update;
    }
}
