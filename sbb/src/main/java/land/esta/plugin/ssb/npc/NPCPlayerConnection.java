package land.esta.plugin.ssb.npc;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Set;

public class NPCPlayerConnection extends PlayerConnection {

    @Override
    public void sendPacket(Packet packet) {
        super.sendPacket(packet);
    }

    public void sendPacket(Packet packet, boolean flush) {
        super.sendPacket(packet);
    }

    public void a(Packet packet, boolean flush) {
        super.sendPacket(packet);
    }

    public NPCPlayerConnection(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
        super(minecraftserver, networkmanager, entityplayer);
    }
}
