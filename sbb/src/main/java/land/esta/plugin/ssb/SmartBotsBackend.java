package land.esta.plugin.ssb;

import gg.scala.commons.ExtendedScalaPlugin;
import gg.scala.commons.annotations.container.ContainerDisable;
import gg.scala.commons.annotations.container.ContainerEnable;
import gg.scala.commons.core.plugin.*;
import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import land.esta.plugin.ssb.bot.setting.setting.persistence.InternalValue;
import land.esta.plugin.ssb.utils.DataManager;
import land.esta.plugin.ssb.utils.GameServer;
import land.esta.plugin.ssb.utils.PacketListener;
import lombok.Getter;
import land.esta.plugin.ssb.botevents.EventManager;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.bots.BotManager;
import land.esta.plugin.ssb.features.FeatureManager;
import land.esta.plugin.ssb.support.SupportManager;
import land.esta.plugin.ssb.thread.ThreadSystem;
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
@Getter
public final class SmartBotsBackend extends ExtendedScalaPlugin {

    @Getter
    public static SmartBotsBackend instance;

    private BotManager botManager;
    private EventManager eventManager;
    private FeatureManager featureManager;
    private SupportManager supportManager;
    private ThreadSystem threadSystem;
    private DataManager dataManager;

    @ContainerEnable
    public void containerEnable() {
        // Plugin startup logic
        instance = this;

        Serializers.INSTANCE.create(gsonBuilder -> {
            gsonBuilder.registerTypeAdapter(
                    InternalValue.class,
                    new AbstractTypeSerializer<>()
            );
            return null;
        });

        // Pre-initializes the root category
        RootAccessor.INSTANCE.getAttackSettings();

        if (GameServer.is()) {
            botManager = new BotManager();
            dataManager = new DataManager(this);

            if (GameServer.is()) {
                Bukkit.getServer().getPluginManager().registerEvents(new PacketListener(), this);
            }
        }

        eventManager = new EventManager();
        supportManager = new SupportManager();

        if (GameServer.is()) {
            featureManager = new FeatureManager();
        }

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
}
