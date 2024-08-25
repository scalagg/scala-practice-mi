package gg.solara.minigame.skywars.shared.cages

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.command.ScalaCommandManager

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
object SchemaCageCommandCustomizers
{
    fun customize(manager: ScalaCommandManager)
    {
        manager.commandCompletions.registerCompletion("schemacages") {
            SchemaCageDataSync.cached().schemas.keys
        }

        manager.commandContexts.registerContext(SchemaCage::class.java) {
            val arg = it.popFirstArg()

            SchemaCageDataSync.cached().schemas[arg.lowercase()]
                ?: throw ConditionFailedException("No schema cage by that name was found.")
        }
    }
}
