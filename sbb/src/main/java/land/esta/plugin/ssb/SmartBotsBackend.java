package land.esta.plugin.ssb;

import com.google.gson.GsonBuilder;
import gg.scala.commons.ExtendedScalaPlugin;
import gg.scala.commons.annotations.container.ContainerDisable;
import gg.scala.commons.annotations.container.ContainerEnable;
import gg.scala.commons.core.plugin.*;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import land.esta.plugin.ssb.bot.setting.setting.GlobalsKt;
import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import land.esta.plugin.ssb.bot.setting.setting.persistence.InternalValue;
import land.esta.plugin.ssb.utils.BukkitEvents;
import land.esta.plugin.ssb.utils.DataManager;
import land.esta.plugin.ssb.utils.GameServer;
import land.esta.plugin.ssb.utils.PacketListener;
import lombok.Getter;
import land.esta.plugin.ssb.botevents.BotEvent;
import land.esta.plugin.ssb.botevents.Event;
import land.esta.plugin.ssb.botevents.EventManager;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.bots.BotManager;
import land.esta.plugin.ssb.features.FeatureManager;
import land.esta.plugin.ssb.support.SupportManager;
import land.esta.plugin.ssb.thread.ThreadSystem;
import land.esta.plugin.ssb.utils.config.ConfigManager;
import net.evilblock.cubed.serializers.Serializers;
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializer;
import org.bukkit.Bukkit;

@Plugin(
        name = "EstaPractice-SmartBots",
        version = "KProd"
)
@PluginAuthorComposite({
        @PluginAuthor("crafticat"),
        @PluginAuthor("GrowlyX")
})
@PluginWebsite("https://esta.land")
@PluginDependencyComposite({
        @PluginDependency("scala-commons"),
        @PluginDependency("Lemon"),
        @PluginDependency("EstaPractice")
})
public final class SmartBotsBackend extends ExtendedScalaPlugin {

    @Getter
    public static SmartBotsBackend instance;

    @Getter
    private BotManager botManager;

    @Getter
    private EventManager eventManager;

    @Getter
    private FeatureManager featureManager;

    @Getter
    private BukkitEvents bukkitEvents;

    @Getter
    private SupportManager supportManager;

    @Getter
    private ThreadSystem threadSystem;

    @Getter
    private DataManager dataManager;

    @ContainerEnable
    public void containerEnable() {
        // Plugin startup logic
        instance = this;
        new ConfigManager(); //Loading the config manager

        Serializers.INSTANCE.create(gsonBuilder -> {
            gsonBuilder.registerTypeAdapter(
                    InternalValue.class,
                    new AbstractTypeSerializer<>()
            );
            return null;
        });

        GlobalsKt.getRoot().getChildren$sbb().values().forEach(setting -> {
            getLogger().info(setting.composeId());
        });
        RootAccessor.INSTANCE.getAttackSettings();

        if (GameServer.is()) {
            botManager = new BotManager();
            dataManager = new DataManager(this);

            if (GameServer.is()) {
                Bukkit.getServer().getPluginManager().registerEvents(new PacketListener(),this);
            }
        }

        eventManager = new EventManager();
        supportManager = new SupportManager();

        if (GameServer.is()) {
            featureManager = new FeatureManager();
        }

        bukkitEvents = new BukkitEvents();

        if (GameServer.is()) {
            threadSystem = new ThreadSystem();
        }
    }

    @ContainerDisable
    public void disable() {
        if (GameServer.is()) {
            for (Bot bot : getBotManager().getBots()) {
                bot.silentKill();
            }
        }

        instance = null;
    }

    public void callEvent(Event event) {
        for (BotEvent events : SmartBotsBackend.getInstance().getEventManager().getEvents()) {
            events.call(event);
        }
    }
}
