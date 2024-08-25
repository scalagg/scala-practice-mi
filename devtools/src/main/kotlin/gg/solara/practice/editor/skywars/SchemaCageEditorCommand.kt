package gg.solara.practice.editor.skywars

import gg.scala.commons.acf.CommandHelp
import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandCompletion
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Default
import gg.scala.commons.acf.annotation.HelpCommand
import gg.scala.commons.acf.annotation.Subcommand
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.solara.practice.minigame.skywars.shared.cages.SchemaCage

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
@AutoRegister
@CommandAlias("schemacage")
@CommandPermission("op")
object SchemaCageEditorCommand : ScalaCommand()
{
    @Default
    @HelpCommand
    fun help(help: CommandHelp) = help.showHelp()

    @Subcommand("edit")
    @CommandCompletion("@schemacages")
    fun edit(player: ScalaPlayer, schemaCage: SchemaCage)
    {
        if (SchemaCageEditorSystem.instances[player.uniqueId] != null)
        {
            throw ConditionFailedException("Already editing!")
        }

        val system = SchemaCageEditorSystem(
            player.bukkit(),
            SchemaCageEditorInstance(schemaCage.id, schemaCage)
        )
        system.initialize()

        SchemaCageEditorSystem.instances[player.uniqueId] = system
    }
}
