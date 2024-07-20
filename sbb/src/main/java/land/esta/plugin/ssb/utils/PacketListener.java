package land.esta.plugin.ssb.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import land.esta.plugin.ssb.SmartBotsBackend;
import lombok.Getter;
import lombok.Setter;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.data.DataPlayer;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.lang.reflect.Field;

@Getter
@Setter
public class PacketListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        removePlayer(e.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        addPlayer(e.getPlayer());
    }

    public void addPlayer(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {

            @Override
            @Deprecated
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                try {
                    if (packet instanceof PacketPlayInUseEntity) {
                        PacketPlayInUseEntity wrapper = (PacketPlayInUseEntity) packet;
                        handleAttack(wrapper, player);
                    }

                    if (packet instanceof PacketPlayInFlying) {
                        DataPlayer data = SmartBotsBackend.getInstance().getDataManager().getPlayer(player);
                        if (data != null) {
                            data.handleFlying((PacketPlayInFlying) packet);
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                super.channelRead(channelHandlerContext, packet);
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
                DataPlayer data = SmartBotsBackend.getInstance().getDataManager().getPlayer(player);

                //OverWrites Player velocity

                if (packet instanceof PacketPlayOutEntityVelocity) {
                    PacketPlayOutEntityVelocity wrapper = (PacketPlayOutEntityVelocity) packet;
                    ByteBuf byteBuf = Unpooled.buffer();
                    PacketDataSerializer packetData = new PacketDataSerializer(byteBuf);

                    wrapper.b(packetData);

                    int id = packetData.e();
                    double x = packetData.readShort() / 8000D;
                    double y = packetData.readShort() / 8000D;
                    double z = packetData.readShort() / 8000D;

                    if (id == player.getEntityId()) {
                        Vector velocity = new Vector(x, y, z);

                        if (data.handle()) {
                            Vector v = data.velocity();
                            packetData = new PacketDataSerializer(byteBuf);
                            packetData.b(id);
                            packetData.writeShort((int) (v.getX() * 8000D));
                            packetData.writeShort((int) (v.getY() * 8000D));
                            packetData.writeShort((int) (v.getZ() * 8000D));

                            wrapper.a(packetData);
                        }
                    }
                }

                //OverWrites Relative move if enabled
                if (packet instanceof PacketPlayOutEntity) {
                    PacketPlayOutEntity wrapper = (PacketPlayOutEntity) packet;
                    ByteBuf byteBuf = Unpooled.buffer();
                    PacketDataSerializer packetData = new PacketDataSerializer(byteBuf);

                    wrapper.b(packetData);
                    int id = packetData.e();

                    Bot bot = SmartBotsBackend.getInstance().getBotManager().getNpcFromTargetView(player);

                    if (bot != null && bot.getNpc().getId() == id && !bot.getPacketsManager().isRelativeMovement())
                        return;
                }

                super.write(channelHandlerContext, packet, channelPromise);
            }
        };

        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        ChannelPipeline pipeline = channel.pipeline();
        if (pipeline == null)
            return;
        String handlerName = "bot_processor";
        channel.eventLoop().submit(() -> {
            if (pipeline.get(handlerName) != null)
                pipeline.remove(handlerName);
            pipeline.addBefore("packet_handler", handlerName, channelDuplexHandler);
            return null;
        });
    }

    public PacketDataSerializer getPacketData(Packet wrapper) throws IOException {
        ByteBuf byteBuf = Unpooled.buffer();
        PacketDataSerializer packetData = new PacketDataSerializer(byteBuf);

        wrapper.b(packetData);
        return packetData;
    }

    public void handleAttack(PacketPlayInUseEntity wrapper,Player player) throws NoSuchFieldException, IllegalAccessException {
        if (!wrapper.a().equals(PacketPlayInUseEntity.EnumEntityUseAction.ATTACK)) return;

        Field f = wrapper.getClass().getDeclaredField("a");
        f.setAccessible(true);

        int id = (int) f.get(wrapper);
        attack(id,player);
    }

    public void attack(int id,Player attacker) {
        if (SmartBotsBackend.getInstance() == null) {
            return;
        }

        for (Bot bot : SmartBotsBackend.getInstance().getBotManager().getBots()) {
            if (bot.getNpc().getId() == id) {
                //Nms already does this for us
                //bot.getDamageManager().damage(attacker,true);
            }
        }
    }

    public void removePlayer(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        ChannelPipeline pipeline = channel.pipeline();
        String handlerName = "bot_processor";
        channel.eventLoop().submit(() -> {
            if(pipeline.get(handlerName) != null)
                pipeline.remove(handlerName);
            return null;
        });
    }
}
