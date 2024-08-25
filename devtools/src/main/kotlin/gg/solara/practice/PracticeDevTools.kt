package gg.solara.practice

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.container.ContainerDisable
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.annotations.container.ContainerPreEnable
import gg.scala.commons.core.plugin.*
import gg.solara.practice.minigame.skywars.shared.cages.SchemaCageCommandCustomizers
import gg.tropic.practice.PracticeShared
import me.lucko.helper.Events
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.event.entity.EntitySpawnEvent
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 9/22/2023
 */
@Plugin(
    name = "TropicPractice-DevTools",
    version = "%remote%/%branch%/%id%"
)
@PluginAuthor("Tropic")
@PluginWebsite("https://tropic.gg")
@PluginDependencyComposite(
    PluginDependency("scala-commons"),
    PluginDependency("Lemon"),
    PluginDependency("WorldEdit"),
    PluginDependency("ScBasics", soft = true)
)
class PracticeDevTools : ExtendedScalaPlugin()
{
    companion object
    {
        @JvmStatic
        var instance by Delegates.notNull<PracticeDevTools>()
    }

    init
    {
        PracticeShared
    }

    @ContainerPreEnable
    fun preEnable()
    {
        instance = this
    }

    @ContainerEnable
    fun containerEnable()
    {
        Bukkit.getWorldContainer().listFiles()
            ?.filter { it.name.startsWith("editor") }
            ?.forEach {
                it.deleteRecursively()
            }

        Events
            .subscribe(EntitySpawnEvent::class.java)
            .handler {
                if (it.entityType != EntityType.PLAYER)
                {
                    return@handler
                }

                it.isCancelled = true
            }
            .bindWith(this)

        SchemaCageCommandCustomizers.customize(commandManager)
    }

    @ContainerDisable
    fun containerDisable()
    {
        Bukkit.getWorlds()
            .filter { it.name.startsWith("editor") }
            .forEach {
                Bukkit.unloadWorld(it, false)
            }
    }
}
