package land.esta.plugin.ssb.bots;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import gg.tropic.practice.games.GameImpl;
import gg.tropic.practice.kit.Kit;
import land.esta.plugin.ssb.EstaRobotsPlugin;
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
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
@Setter
public class Bot {
    private MovementsMain movements;
    private NPCEntity npc;
    private Kit kit;
    private PacketsManager packetsManager;
    private ViewsManager viewsManager;
    private EffectsManager effectsManager;
    private VelocityCalculator velocityCalculator;
    private MEventManager eventManager;
    private DamageManager damageManager;
    private InventoryManager inventoryManager;
    private AttackingManager attackingManager;
    private HealthManager healthManager;
    private LagCompensatorManager lagCompensatorManager;
    private FleeManager fleeManager;
    private EventRunnable eventListener = (event) -> {};
    private DamageCalculator damageCalculator;

    private boolean alive = true;
    private final UUID uniqueId = UUID.randomUUID();
    private final CompositeTerminable terminable = CompositeTerminable.create();

    private final Player player;
    private final SettingProfile profile;
    private Location startLocation;

    private int combo = 0, ticks = 0;

    /**
     * Keeps track of how many potions have been used in the hotbar
     * so we can trigger the refill pause system.
     */
    private int potionsThrown = 0;
    private GameImpl game;

    private boolean currentlyBuilding;

    // Temp
    private long lastPlaced = System.currentTimeMillis();
    private List<Location> blockedLocations = new ArrayList<>();
    private long lastPlacedBlockUp = System.currentTimeMillis();
    private long lastLiquid = System.currentTimeMillis();
//    private List<Island> islands = new ArrayList<>();

    public Bot(Location location, String name, Player player, GameImpl game, SettingProfile profile, Kit kit) {
        this.startLocation = location;
        this.kit = kit;
        this.profile = profile;
        this.player = player;
        this.game = game;

        registerNPC(name, location);
        Bukkit.getOnlinePlayers().forEach(other ->
                other.hidePlayer(npc.getBukkitEntity())
        );

        registerManagers(player, location);

        packetsManager.sendCreatesPackets();

        loadManagers();
        player.showPlayer(npc.getBukkitEntity());

        registerBot();
    }

    private void registerBot() {
        EstaRobotsPlugin.getInstance().getBotManager().addBot(this);
        movements.callEventAsync(new SpawnEvent(this));
    }

    private void loadManagers() {
        eventManager.addEvent(movements);
        eventManager.addEvent(viewsManager);
        eventManager.addEvent(inventoryManager);
        eventManager.addEvent(packetsManager);
        eventManager.addEvent(damageManager = new DamageManager(this));
        eventManager.addEvent(attackingManager = new AttackingManager(this));
        eventManager.addEvent(healthManager = new HealthManager(this));
        eventManager.addEvent(damageCalculator = new DamageCalculator(this));
        eventManager.addEvent(fleeManager);
        eventManager.addEvent(effectsManager);
    }

    private void registerManagers(Player player, Location location) {
        lagCompensatorManager = new LagCompensatorManager(this);
        eventManager = new MEventManager(this);
        effectsManager = new EffectsManager(this);
        velocityCalculator = new VelocityCalculator(this);
        movements = new MovementsMain(this, player, location);
        packetsManager = new PacketsManager(this);
        inventoryManager = new InventoryManager(this);
        fleeManager = new FleeManager(this);

        final List<Player> players = new ArrayList<>();
        players.add(player);
        viewsManager = new ViewsManager(this, players);
    }

    private void registerNPC(String name, Location loc) {
        GameProfile profile = new GameProfile(uniqueId, name);
        profile.getProperties().put("textures", this.profile.getSkinProperties());

        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
        PlayerInteractManager interactManager = new PlayerInteractManager(world);

        npc = new NPCEntity(server, world, profile, interactManager);
        npc.playerConnection = new NPCPlayerConnection(server, new NPCNetworkManager(EnumProtocolDirection.CLIENTBOUND), npc);
        npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        npc.setPosition(loc.getX(), loc.getY(), loc.getZ());

        Schedulers
                .sync()
                .run(() -> world.addEntity(npc))
                .bindWith(terminable);
    }

    public static final Property DEFAULT = new Property(
            "textures",
            "ewogICJ0aW1lc3RhbXAiIDogMTcyMTU5ODQ0MzU4MiwKICAicHJvZmlsZUlkIiA6ICI5OGYzMGY2OWExYTA0NWRhOWNmOGZjMDI3OThmOWEyZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJDaGVtaWNhbExvY2tlZCIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82MmI1Mjk3ZjhjOTg1YTBkODU5ZjY5Zjc0YzE3ODAzMTcwZjFhMmU4NTk1ZGEyZWUzZWU4Nzk1N2VkMzQzZTMzIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
            ""
    );

    public void kill() {
        if (!alive) {
            return;
        }

        attackingManager.setAttacking(false);
        damageManager.setInvaluable(true);

        //Animation
        fakeDeath();

        //Event
        Bukkit.getServer().getPluginManager().callEvent(new BotDeathEvent(this));
    }

    public void silentKill() {
        if (alive) {
            destroy();
        }
    }

    public void fakeDeath() {
        movements.setMove(false);
        packetsManager.sendDeathPackets();

        Schedulers.sync().runLater(this::destroy, 14L);
    }

    public void destroy() {
        alive = false;
        packetsManager.sendRemovePackets();
        EstaRobotsPlugin.getInstance().getBotManager().removeBot(this);

        terminable.closeAndReportException();

        npc.getBukkitEntity().remove();
        Bukkit.getServer().getPluginManager().callEvent(new BotDeathEvent(this));
    }

    public void tick() {
        velocityCalculator.setEarly(true);

        eventManager.queueUpdate();
        eventManager.tickEarly();
        eventManager.tick();
        eventManager.tickLate();

        velocityCalculator.setEarly(false);
        eventManager.callEvents();
    }
}
