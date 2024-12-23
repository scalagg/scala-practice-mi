package gg.scala.practice.commands

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.practice.menu.editor.EditorKitSelectionMenu
import gg.scala.practice.player.LobbyPlayerService
import gg.scala.practice.player.PlayerState
import gg.scala.practice.profile.PracticeProfileService

@AutoRegister
object KitEditorCommand : ScalaCommand()
{
    @CommandAlias("kiteditor")
    fun onKitEditor(player: ScalaPlayer)
    {
        val profile = PracticeProfileService.find(player.uniqueId)
            ?: throw ConditionFailedException(
                "You profile doesn't exist!"
            )

        with(LobbyPlayerService.find(player.bukkit())) {
            if (this?.state != PlayerState.Idle)
            {
                throw ConditionFailedException(
                    "You must be at spawn to enter the kit editor!"
                )
            }
        }

        EditorKitSelectionMenu(player.bukkit(), profile).openMenu(player.bukkit())
    }
}
