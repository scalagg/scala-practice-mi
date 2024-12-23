package gg.scala.practice

import gg.scala.basics.plugin.profile.BasicsProfileService
import gg.scala.basics.plugin.settings.SettingMenu
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.core.plugin.*
import gg.scala.commons.preconfigure.PreConfigureSubTypeProcessor
import gg.scala.lemon.channel.ChatChannelService
import gg.scala.lemon.redirection.aggregate.ServerAggregateHandler
import gg.scala.lemon.redirection.aggregate.impl.LeastTrafficServerAggregateHandler
import gg.scala.practice.games.GameService
import gg.scala.practice.lifecycle.NightlyRebootScheduler
import gg.scala.practice.minigame.*
import gg.scala.practice.settings.ChatVisibility
import gg.scala.practice.settings.DuelsSettingCategory
import org.bukkit.Bukkit

/**
 * @author GrowlyX
 * @since 8/4/2022
 */
@Plugin(
    name = "ScalaPractice",
    version = "%remote%/%branch%/%id%"
)
 @PluginAuthor("Scala")
@PluginWebsite("https://scala.gg")
@PluginDependencyComposite(
    PluginDependency("scala-commons"),
    PluginDependency("Lemon"),
    PluginDependency("SlimeWorldManager"),
    PluginDependency("CoreGameExtensions"),
    PluginDependency("ScBasics"),
    PluginDependency("Parties"),
    PluginDependency("SPA"),
    PluginDependency("UnifiedMetrics", soft = true),
    PluginDependency("cloudsync", soft = true),
    PluginDependency("Friends", soft = true),
    PluginDependency("ScStaff", soft = true),
    PluginDependency("Apollo-Bukkit", soft = true)
)
class PracticeGame : ExtendedScalaPlugin()
{
    init
    {
        PracticeShared
    }

    @ContainerEnable
    fun containerEnable()
    {
        devProvider = {
            "mipgamedev" in ServerSync.getLocalGameServer().groups
        }

        MiniGameSerializers.configure()
        PreConfigureSubTypeProcessor.register<BasicMiniGameOrchestrator<*>> {
            MiniGameRegistry.miniGameOrchestrators[it.id] = it
        }

        ChatChannelService.default
            .displayToPlayer { player, other ->
                val chatVisibility = BasicsProfileService.find(other)
                    ?.setting(
                        "${DuelsSettingCategory.DUEL_SETTING_PREFIX}:chat-visibility",
                        ChatVisibility.Global
                    )
                    ?: ChatVisibility.Global

                val bukkitPlayer = Bukkit.getPlayer(player)
                val game = GameService.byPlayerOrSpectator(player)
                if (game is AbstractMiniGameGameImpl<*>)
                {
                    return@displayToPlayer bukkitPlayer.world.name == other.world.name
                }

                when (chatVisibility)
                {
                    ChatVisibility.Global -> true
                    else -> bukkitPlayer != null && bukkitPlayer.world.name == other.world.name
                }
            }

        val lobbyRedirector = LeastTrafficServerAggregateHandler(
            lobbyGroup().suffixWhenDev()
        )
        lobbyRedirector.subscribe()

        NightlyRebootScheduler.scheduleReboot(1, 0)
        SettingMenu.defaultCategory = "Practice"

        flavor {
            bind<ServerAggregateHandler>() to lobbyRedirector
        }
    }
}
