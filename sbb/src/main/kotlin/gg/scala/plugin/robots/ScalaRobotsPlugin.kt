package gg.scala.plugin.robots

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.container.ContainerDisable
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.core.plugin.*
import gg.scala.practice.menu.party.PartyPlayBotsIntegration
import gg.scala.practice.player.hotbar.LobbyHotbarService.buildNewBotFightMenu
import gg.scala.plugin.robots.bot.menu.BotFightMenu
import gg.scala.plugin.robots.bot.menu.PartyBotFightPrepareMenu
import gg.scala.plugin.robots.bot.menu.SelectBotFightMenu
import gg.scala.plugin.robots.bot.menu.profiles.GenericDescribeProfilesMenu
import gg.scala.plugin.robots.bot.setting.BotSettingService
import gg.scala.plugin.robots.bot.setting.setting.RootAccessor.attackSettings
import gg.scala.plugin.robots.bot.setting.setting.persistence.InternalValue
import gg.scala.plugin.robots.bots.BotManager
import gg.scala.plugin.robots.bots.events.EventManager
import gg.scala.plugin.robots.features.FeatureManager
import gg.scala.plugin.robots.scala.ScalaPracticeSupport
import gg.scala.plugin.robots.utils.DataManager
import gg.scala.plugin.robots.utils.GameServer
import gg.scala.plugin.robots.utils.PacketListener
import gg.scala.practice.player.hotbar.LobbyHotbarService.buildBotSelectorMenu
import net.evilblock.cubed.serializers.Serializers.create
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializer
import org.bukkit.Bukkit

@Plugin(
    name = "Practice-SmartBots",
    version = "%remote%/%branch%/%id%"
)
@PluginAuthorComposite(
    PluginAuthor("Scala"),
)
@PluginWebsite("https://scala.gg")
@PluginDependencyComposite(
    PluginDependency("scala-commons"),
    PluginDependency("Lemon"),
    PluginDependency("Practice")
)
class ScalaRobotsPlugin : ExtendedScalaPlugin()
{
    companion object
    {
        @JvmStatic
        lateinit var instance: ScalaRobotsPlugin
    }

    lateinit var botManager: BotManager
    lateinit var eventManager: EventManager
    lateinit var featureManager: _root_ide_package_.gg.scala.plugin.robots.features.FeatureManager
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
            ScalaPracticeSupport.enable()
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
