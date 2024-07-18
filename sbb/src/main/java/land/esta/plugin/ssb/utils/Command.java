package land.esta.plugin.ssb.utils;

import land.esta.plugin.ssb.SmartBotsBackend;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import land.esta.plugin.ssb.utils.objects.PlayerRunnable;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Getter
@Setter
public class Command extends BukkitCommand {

    private PlayerRunnable onCommand;
    private String arg;
    private String[] args;
    private Player player;

    public Command(String name, String description, String usageMessage, PlayerRunnable command) {
        super(name);
        this.description = description;
        this.usageMessage = usageMessage;
        this.setAliases(new ArrayList<>());
        this.onCommand = command;
        ((CraftServer) SmartBotsBackend.getInstance().getServer()).getCommandMap().register(name, this);
    }

    @SneakyThrows
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            this.arg = commandLabel;
            this.args = args;
            this.player = player;
            onCommand.run(player, 0);
        }
        return false;
    }
}
