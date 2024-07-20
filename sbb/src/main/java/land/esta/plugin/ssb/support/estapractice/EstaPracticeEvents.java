package land.esta.plugin.ssb.support.estapractice;

import land.esta.plugin.ssb.SmartBotsBackend;
import lombok.Getter;
import land.esta.plugin.ssb.utils.Nms;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@Getter
public class EstaPracticeEvents implements Listener {

    //If you have StrikePractice you are also required to have Citizens, so we could replace the bot.

    private final EstaPracticeSupport support;

    public EstaPracticeEvents(EstaPracticeSupport support) {
        this.support = support;
        Bukkit.getServer().getPluginManager().registerEvents(this, SmartBotsBackend.getInstance());
    }


//    @EventHandler
//    public void botDuelStartEvent(BotDuelStartEvent e) throws IOException {
//        Location loc = e.getBot().getNpc().getBukkitEntity().getLocation();
//        BattleKit kit = e.getFight().getKit();
//        List<ItemStack> inv = kit.getInventory();
//        ItemStack helmet = kit.getHelmet();
//        ItemStack chestPlate = kit.getChestplate();
//        ItemStack leggings = kit.getLeggings();
//        ItemStack boots = kit.getBoots();
//        Collection<PotionEffect> effects = kit.getPotions();
//        boolean combo = kit.isCombo();
//
//        if (e.getBot() != null) {
//            if (e.getBot() != null && e.getBot().getNpc() != null) {
//                e.getBot().getNpc().getBukkitEntity().remove();
////                e.getBot().despawn();
////                e.getBot().destroy();
//            }
//
//            if (e.getBot().getNpc().getBukkitEntity() instanceof CraftPlayer) {
//                removeNPCPacket(((CraftPlayer) e.getBot().getNpc().getBukkitEntity()).getHandle());
//            }
//        }
//
//        support.getManager().spawn(loc,inv,helmet,chestPlate,leggings,boots,effects,combo,e.getPlayer(),e.getFight().getKit().isBoxing(),e.getFight()); // Add e.getTier() in the last
//
//    }

//    @EventHandler
//    public void botDuelStartEvent(RoundStartEvent e) {
//        if (e.getFight() instanceof BotDuel) {
//            BotDuel duel = (BotDuel) e.getFight();
//            Player player =  Bukkit.getPlayer(duel.getP1());
//            Bot bot = SmartBotsBackend.getInstance().getBotManager().getNpcFromTarget(player);
//            if (bot != null) bot.silentKill();
//            if (e.getBestOf().getRounds() <= 1) return;
//            if (e.getBestOf().getCurrentRound() <= 1) return;
//
//            new BukkitRunnable() {
//                @SneakyThrows
//                @Override
//                public void run() {
//                    spawnBot(duel, player,e.getFight().getArena().getLoc2());
//                }
//            }.runTaskLater(SmartBotsBackend.getInstance(),1);
//        }
//    }

//    public void spawnBot(BotDuel fight,Player player,Location loc) throws IOException {
//        BattleKit kit = fight.getKit();
//        List<ItemStack> inv = kit.getInventory();
//        ItemStack helmet = kit.getHelmet();
//        ItemStack chestPlate = kit.getChestplate();
//        ItemStack leggings = kit.getLeggings();
//        ItemStack boots = kit.getBoots();
//        Collection<PotionEffect> effects = kit.getPotions();
//        boolean combo = kit.isCombo();
//
//        fight.getP2().getNpc().getBukkitEntity().remove();
////        fight.getP2().getNPC().despawn();
////        fight.getP2().getNPC().destroy();
////        fight.getP2().destroy();
//
//        if (fight.getP2().getNpc().getBukkitEntity() instanceof CraftPlayer) {
//            removeNPCPacket(((CraftPlayer) fight.getP2().getNpc().getBukkitEntity()).getHandle());
//        }
//
//        Bot bot = support.getManager().spawn(loc,inv,helmet,chestPlate,leggings,boots,effects,combo,player,fight.getKit().isBoxing(),fight); // Add e.getTier() in the last
//        if (bot == null) return;
//        fight.getSpectators().forEach(player1 -> {
//            bot.getViewsManager().addPlayer(player1);
//        });
//    }


    /*@EventHandler
    public void spec(PlayerStartSpectatingEvent e) {
        Player player = e.getPlayer();
        e.getFight().getPlayersInFight().forEach(p -> {
            Bot bot = SmartBotsBackend.getInstance().getBotManager().getNpcFromTarget(p);
            if (bot != null) bot.getViewsManager().addPlayer(player);
        });
    }
*/

    public void removeNPCPacket(EntityPlayer entity) {
        for (Player player : Bukkit.getOnlinePlayers()) Nms.sendPacket(player,new PacketPlayOutEntityDestroy(entity.getId()));
        for (Player player : Bukkit.getOnlinePlayers()) Nms.sendPacket(player,new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entity));
    }
}
