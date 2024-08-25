package gg.solara.practice.minigame.skywars.customizer

import gg.scala.commons.annotations.commands.customizer.CommandManagerCustomizer
import gg.scala.commons.command.ScalaCommandManager
import gg.solara.practice.minigame.skywars.shared.cages.SchemaCageCommandCustomizers

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
object SkyWarsCommandCustomizer
{
    @CommandManagerCustomizer
    fun customize(manager: ScalaCommandManager) = SchemaCageCommandCustomizers.customize(manager)
}
