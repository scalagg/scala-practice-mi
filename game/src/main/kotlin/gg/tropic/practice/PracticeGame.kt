package gg.tropic.practice

import gg.scala.basics.plugin.profile.BasicsProfileService
import gg.scala.basics.plugin.shutdown.ShutdownService
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.core.plugin.*
import gg.scala.commons.preconfigure.PreConfigureSubTypeProcessor
import gg.scala.lemon.channel.ChatChannelService
import gg.scala.lemon.redirection.aggregate.ServerAggregateHandler
import gg.scala.lemon.redirection.aggregate.impl.LeastTrafficServerAggregateHandler
import gg.tropic.practice.lifecycle.NightlyRebootScheduler
import gg.tropic.practice.minigame.MiniGame
import gg.tropic.practice.minigame.MiniGameRegistry
import gg.tropic.practice.settings.ChatVisibility
import gg.tropic.practice.settings.DuelsSettingCategory
import me.lucko.helper.Schedulers
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.time.TimeUtil
import org.bukkit.Bukkit
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 8/4/2022
 */
@Plugin(
    name = "SolaraPractice",
    version = "%remote%/%branch%/%id%"
)
@PluginAuthor("Tropic")
@PluginWebsite("https://tropic.gg")
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

        PreConfigureSubTypeProcessor.register<MiniGame> {
            MiniGameRegistry.miniGames[it.id()] = it
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
                when (chatVisibility)
                {
                    ChatVisibility.Global -> true
                    ChatVisibility.Match -> bukkitPlayer != null && bukkitPlayer.world.name == other.world.name
                }
            }

        val lobbyRedirector = LeastTrafficServerAggregateHandler(
            lobbyGroup().suffixWhenDev()
        )
        lobbyRedirector.subscribe()

        NightlyRebootScheduler.scheduleReboot(1, 0)

        flavor {
            bind<ServerAggregateHandler>() to lobbyRedirector
        }
    }
}
