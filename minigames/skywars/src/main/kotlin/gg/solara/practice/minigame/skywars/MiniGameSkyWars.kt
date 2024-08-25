package gg.solara.practice.minigame.skywars

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.core.plugin.*

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
@Plugin(
    name = "SolaraPractice-MiniGame-SkyWars",
    version = "%remote%/%branch%/%id%"
)
@PluginAuthorComposite(
    PluginAuthor("GrowlyX")
)
@PluginWebsite("https://solara.gg")
@PluginDependencyComposite(
    PluginDependency("scala-commons"),
    PluginDependency("Lemon"),
    PluginDependency("SolaraPractice")
)
class MiniGameSkyWars : ExtendedScalaPlugin()
