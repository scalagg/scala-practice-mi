package land.esta.plugin.ssb

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.container.ContainerDisable
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.core.plugin.*
import gg.tropic.practice.menu.party.PartyPlayBotsIntegration
import gg.tropic.practice.player.hotbar.LobbyHotbarService.buildNewBotFightMenu
import land.esta.plugin.ssb.bot.menu.BotFightMenu
import land.esta.plugin.ssb.bot.menu.PartyBotFightPrepareMenu
import land.esta.plugin.ssb.bot.menu.openMenuProtected
import land.esta.plugin.ssb.bot.menu.profiles.GenericDescribeProfilesMenu
import land.esta.plugin.ssb.bot.menu.profiles.ProfileBackbone
import land.esta.plugin.ssb.bot.menu.profiles.ProfileCategoryMenu
import land.esta.plugin.ssb.bot.setting.BotSettingService
import land.esta.plugin.ssb.bot.setting.setting.RootAccessor.attackSettings
import land.esta.plugin.ssb.bot.setting.setting.persistence.InternalValue
import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile
import land.esta.plugin.ssb.bot.setting.setting.root
import land.esta.plugin.ssb.botevents.EventManager
import land.esta.plugin.ssb.bots.BotManager
import land.esta.plugin.ssb.features.FeatureManager
import land.esta.plugin.ssb.solara.SolaraPracticeSupport
import land.esta.plugin.ssb.utils.DataManager
import land.esta.plugin.ssb.utils.GameServer
import land.esta.plugin.ssb.utils.PacketListener
import net.evilblock.cubed.serializers.Serializers.create
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializer
import org.bukkit.Bukkit

@Plugin(
    name = "SolaraPractice-SmartBots",
    version = "%remote%/%branch%/%id%"
)
@PluginAuthorComposite(
    PluginAuthor("crafticat"),
    PluginAuthor("GrowlyX"),
    PluginAuthor("TastyCake")
)
@PluginWebsite("https://solara.gg")
@PluginDependencyComposite(
    PluginDependency("scala-commons"),
    PluginDependency("Lemon"),
    PluginDependency("SolaraPractice")
)
class SolaraRobotsPlugin : ExtendedScalaPlugin()
{
    companion object
    {
        @JvmStatic
        var instance: SolaraRobotsPlugin? = null
    }

    lateinit var botManager: BotManager
    lateinit var eventManager: EventManager
    lateinit var featureManager: FeatureManager
    lateinit var dataManager: DataManager

    @ContainerEnable
    fun containerEnable()
    {
        // Plugin startup logic
        instance = this

        create {
            registerTypeAdapter(
                InternalValue::class.java,
                AbstractTypeSerializer<AbstractTypeSerializable>()
            )
        }

        // Pre-initializes the root category
        attackSettings

        if (GameServer.`is`())
        {
            botManager = BotManager(this)
            dataManager = DataManager(this)

            if (GameServer.`is`())
            {
                Bukkit.getServer().pluginManager.registerEvents(PacketListener(), this)
            }
        }

        eventManager = EventManager()

        if (GameServer.`is`())
        {
            featureManager = FeatureManager()
            SolaraPracticeSupport.enable()
        } else
        {
            PartyPlayBotsIntegration.createBotsWith = { player, players ->
                GenericDescribeProfilesMenu(
                    {
                        BotSettingService
                            .cached()
                            .map
                            .values
                            .toList()
                    },
                    { settingProfile ->
                        PartyBotFightPrepareMenu(players, settingProfile.id).openMenu(player)
                    }
                ).openMenu(player)
            }

            buildNewBotFightMenu = {
                BotFightMenu(it)
            }
        }
    }

    @ContainerDisable
    fun containerDisable()
    {
        if (GameServer.`is`())
        {
            for (bot in botManager.bots)
            {
                bot.destroy()
            }
        }

        instance = null
    }
}
