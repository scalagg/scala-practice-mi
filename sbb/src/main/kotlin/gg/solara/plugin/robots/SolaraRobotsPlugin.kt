package gg.solara.plugin.robots

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.container.ContainerDisable
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.core.plugin.*
import gg.tropic.practice.menu.party.PartyPlayBotsIntegration
import gg.tropic.practice.player.hotbar.LobbyHotbarService.buildNewBotFightMenu
import gg.solara.plugin.robots.bot.menu.BotFightMenu
import gg.solara.plugin.robots.bot.menu.PartyBotFightPrepareMenu
import gg.solara.plugin.robots.bot.menu.SelectBotFightMenu
import gg.solara.plugin.robots.bot.menu.profiles.GenericDescribeProfilesMenu
import gg.solara.plugin.robots.bot.setting.BotSettingService
import gg.solara.plugin.robots.bot.setting.setting.RootAccessor.attackSettings
import land.esta.plugin.ssb.bot.setting.setting.persistence.InternalValue
import gg.solara.plugin.robots.bots.events.EventManager
import gg.solara.plugin.robots.bots.BotManager
import gg.solara.plugin.robots.features.FeatureManager
import gg.solara.plugin.robots.solara.SolaraPracticeSupport
import gg.solara.plugin.robots.utils.DataManager
import gg.solara.plugin.robots.utils.GameServer
import gg.solara.plugin.robots.utils.PacketListener
import gg.tropic.practice.player.hotbar.LobbyHotbarService.buildBotSelectorMenu
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
        lateinit var instance: SolaraRobotsPlugin
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

            Bukkit.getServer().pluginManager.registerEvents(PacketListener(), this)

            eventManager = EventManager()
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

            buildBotSelectorMenu = {
                SelectBotFightMenu()
            }
        }
    }

    @ContainerDisable
    fun containerDisable()
    {
        if (GameServer.`is`())
        {
            for (bot in botManager.bots.values)
            {
                bot.destroy()
            }
        }
    }
}
