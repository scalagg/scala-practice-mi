package land.esta.plugin.ssb.bots;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import gg.tropic.practice.kit.Kit;
import land.esta.plugin.ssb.SmartBotsBackend;
import land.esta.plugin.ssb.bot.MojangKt;
import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile;
import land.esta.plugin.ssb.bots.mechanics.*;
import lombok.Getter;
import lombok.Setter;
import land.esta.plugin.ssb.botevents.events.BotDeathEvent;
import land.esta.plugin.ssb.botevents.events.SpawnEvent;
import land.esta.plugin.ssb.bots.events.MEventManager;
import land.esta.plugin.ssb.bots.mechanics.movementsmanagement.MovementsMain;
import land.esta.plugin.ssb.npc.NPCEntity;
import land.esta.plugin.ssb.npc.NPCNetworkManager;
import land.esta.plugin.ssb.npc.NPCPlayerConnection;
import land.esta.plugin.ssb.utils.objects.EventRunnable;
import net.evilblock.cubed.util.bukkit.Tasks;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class Bot {
    private MovementsMain movements;
    public NPCEntity npc;
    private Kit kit;
    private PacketsManager packetsManager;
    public ViewsManager viewsManager;
    private EffectsManager effectsManager;
    private VelocityCalculator velocityCalculator;
    private MEventManager MEventManager;
    private DamageManager damageManager;
    private InventoryManager inventoryManager;
    private AttackingManager attackingManager;
    private HealthManager healthManager;
    private LagCompensatorManager lagCompensatorManager;
    private FleeManager fleeManager;
    private EventRunnable eventListener;
    private DamageCalculator damageCalculator;
    private boolean alive = true;
    public boolean killed;

    public final UUID uniqueId = UUID.randomUUID();
    private final Player player;

    public final SettingProfile profile;

    private Location startLocation;

    public Bot(Location loc, String name, Player player, SettingProfile profile, Kit kit) {
        this.startLocation = loc;

        this.kit = kit;
        this.eventListener = (e) -> {
        };

        this.profile = profile;
        this.player = player;
        //Registering
        registerNPC(name, loc);

        Bukkit.getOnlinePlayers().forEach(other -> other.hidePlayer(npc.getBukkitEntity()));

        registerManagers(player, loc);

        //Create packets
        packetsManager.sendCreatesPackets();

        loadManagers();
        player.showPlayer(npc.getBukkitEntity());

        npc.getProfile().getProperties().put("textures", profile.getSkinProperties());
        registerBot();
    }

    private void registerBot() {
        SmartBotsBackend.getInstance().getBotManager().addBot(this);
        movements.callEventAsync(new SpawnEvent(this));
    }

    private void loadManagers() {
        MEventManager.addEvent(movements);
        MEventManager.addEvent(viewsManager);
        MEventManager.addEvent(inventoryManager);
        MEventManager.addEvent(packetsManager);
        MEventManager.addEvent(damageManager = new DamageManager(this));
        MEventManager.addEvent(attackingManager = new AttackingManager(this));
        MEventManager.addEvent(healthManager = new HealthManager(this));
        MEventManager.addEvent(damageCalculator = new DamageCalculator(this));
        MEventManager.addEvent(fleeManager);
        MEventManager.addEvent(effectsManager);
    }

    private void registerManagers(Player pla, Location loc) {
        lagCompensatorManager = new LagCompensatorManager(this);
        MEventManager = new MEventManager(this);
        effectsManager = new EffectsManager(this);
        velocityCalculator = new VelocityCalculator(this);
        movements = new MovementsMain(this, pla, loc);
        packetsManager = new PacketsManager(this);
        inventoryManager = new InventoryManager(this);
        fleeManager = new FleeManager(this);

        final List<Player> players = new ArrayList<>();
        players.add(pla);
        viewsManager = new ViewsManager(this, players);
    }

    private void registerNPC(String name, Location loc) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
        PlayerInteractManager interactManager = new PlayerInteractManager(world);

        npc = new NPCEntity(server, new WeakReference<>(world), profile, interactManager);
        npc.playerConnection = new NPCPlayerConnection(server, new NPCNetworkManager(EnumProtocolDirection.CLIENTBOUND), npc);
        npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        npc.setPosition(loc.getX(), loc.getY(), loc.getZ());

        new BukkitRunnable() {
            @Override
            public void run() {
                world.addEntity(npc);
            }
        }.runTask(SmartBotsBackend.getInstance());
    }

    public static final Property DEFAULT = new Property(
            "textures",
            "ewogICJ0aW1lc3RhbXAiIDogMTcyMTU5ODQ0MzU4MiwKICAicHJvZmlsZUlkIiA6ICI5OGYzMGY2OWExYTA0NWRhOWNmOGZjMDI3OThmOWEyZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJDaGVtaWNhbExvY2tlZCIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82MmI1Mjk3ZjhjOTg1YTBkODU5ZjY5Zjc0YzE3ODAzMTcwZjFhMmU4NTk1ZGEyZWUzZWU4Nzk1N2VkMzQzZTMzIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
            ""
    );

    public void kill() {
        if (killed) return;
        killed = true;

        //Disables attacking
        attackingManager.setAttacking(false);
        damageManager.setInvaluable(true);

        //Calls the animation with the event
        deathEvent();
    }

    public void silentKill() {
        if (!alive) {
            return;
        }

        alive = false;
        Bukkit.getServer().getPluginManager().callEvent(new BotDeathEvent(this));
        SmartBotsBackend.getInstance().getBotManager().removeBot(this);
        packetsManager.sendRemovePackets();

        npc.getBukkitEntity().remove();
        npc = null;
    }

    public void deathEvent() {
        //Animation
        fakeDeath();

        //Event
        Bukkit.getServer().getPluginManager().callEvent(new BotDeathEvent(this));
    }

    public void fakeDeath() {
        movements.setMove(false);
        packetsManager.sendDeathPackets();

        new BukkitRunnable() {
            @Override
            public void run() {
                packetsManager.sendRemovePackets();
                SmartBotsBackend.getInstance().getBotManager().removeBot(Bot.this);

                alive = false;
                npc.getBukkitEntity().remove();
                npc = null;

                Bukkit.getServer().getPluginManager().callEvent(new BotDeathEvent(Bot.this));
            }
        }.runTaskLater(SmartBotsBackend.getInstance(), 14);
    }

    public void tick() {
        velocityCalculator.setPre(true);
        MEventManager.queueUpdate();
        MEventManager.tickPre();
        MEventManager.tickMid();
        MEventManager.tickPost();

        if (!movements.getTargetLocate().getTargetEntity().getWorld().equals(movements.getLocation().getWorld()))
            return;
        velocityCalculator.setPre(false);
        MEventManager.callEvents();
    }
}
