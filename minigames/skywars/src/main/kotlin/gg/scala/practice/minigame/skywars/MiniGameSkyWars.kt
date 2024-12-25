package gg.scala.practice.minigame.skywars

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.core.plugin.*

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
@Plugin(
    name = "ScalaPractice-MiniGame-SkyWars",
    version = "%remote%/%branch%/%id%"
)
@PluginAuthorComposite(
    PluginAuthor("Scala")
)
@PluginWebsite("https://scala.gg")
@PluginDependencyComposite(
    PluginDependency("scala-commons"),
    PluginDependency("Lemon"),
    PluginDependency("Practice")
)
class MiniGameSkyWars : ExtendedScalaPlugin()
