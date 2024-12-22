package gg.solara.practice.minigame.hctranked

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.core.plugin.*

/**
 * @author Topu
 * @date 10/2/2024
 */
@Plugin(
    name = "SolaraPractice-MiniGame-HCTRanked",
    version = "%remote%/%branch%/%id%"
)
@PluginAuthorComposite(
    PluginAuthor("Topu")
)
@PluginWebsite("https://solara.gg")
@PluginDependencyComposite(
    PluginDependency("scala-commons"),
    PluginDependency("Lemon"),
    PluginDependency("SolaraPractice")
)
class MiniGameHCTRanked : ExtendedScalaPlugin()