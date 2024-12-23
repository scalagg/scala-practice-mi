package gg.scala.practice

import gg.scala.basics.plugin.settings.SettingMenu
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.core.plugin.*
import gg.scala.practice.lifecycle.NightlyRebootScheduler
import gg.scala.practice.minigame.MiniGameSerializers
import gg.scala.practice.provider.SettingProvider
import gg.scala.practice.provider.impl.LemonSettingProvider
import gg.scala.practice.services.GameManagerService

/**
 * @author GrowlyX
 * @since 8/5/2022
 */
@Plugin(
    name = "Practice",
    version = "%remote%/%branch%/%id%"
)
 @PluginAuthor("Scala")
@PluginWebsite("https://scala.gg")
@PluginDependencyComposite(
    PluginDependency("scala-commons"),
    PluginDependency("Lemon"),
    PluginDependency("ScBasics"),
    PluginDependency("Parties"),
    PluginDependency("CoreGameExtensions"),
    PluginDependency("ScStaff", soft = true),
    PluginDependency("Friends", soft = true),
)
class PracticeLobby : ExtendedScalaPlugin()
{
    var settingProvider: SettingProvider = LemonSettingProvider

    init
    {
        PracticeShared
    }

    @ContainerEnable
    fun containerEnable()
    {
        devProvider = {
            "miplobbydev" in ServerSync.getLocalGameServer().groups
        }

        MiniGameSerializers.configure()
        NightlyRebootScheduler.scheduleReboot(1, 35)

        SettingMenu.defaultCategory = "Practice"
        GameManagerService.bindToMetadataService()
    }
}
