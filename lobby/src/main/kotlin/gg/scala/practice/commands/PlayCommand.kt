package gg.scala.practice.commands

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.Single
import gg.scala.commons.acf.annotation.Syntax
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.practice.Globals
import gg.scala.practice.games.bots.BotGameMetadata
import gg.scala.practice.games.bots.storeForUser
import gg.scala.practice.kit.Kit
import gg.scala.practice.player.LobbyPlayerService
import gg.scala.practice.player.PlayerState
import gg.scala.practice.queue.QueueService
import gg.scala.practice.queue.QueueType
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 8/17/2024
 */
@AutoRegister
object PlayCommand : ScalaCommand()
{
    @Syntax("<bot>")
    @CommandAlias("play")
    fun play(player: ScalaPlayer, @Single difficulty: String, kit: Kit): CompletableFuture<Void>
    {
        val lobbyProfile = LobbyPlayerService.find(player.bukkit())
            ?: throw ConditionFailedException("You cannot do this right now!")

        if (lobbyProfile.state != PlayerState.Idle)
        {
            throw ConditionFailedException("You cannot do this right now!")
        }

        return CompletableFuture.runAsync {
            BotGameMetadata(
                difficulty,
                null,
                kit.id,
                null,
                botInstances = setOf(
                    Globals.POSSIBLE_PLAYER_BOT_UNIQUE_IDS[0],
                    Globals.POSSIBLE_PLAYER_BOT_UNIQUE_IDS[1]
                )
            ).storeForUser(player.uniqueId)
        }.thenAccept {
            QueueService.joinQueue(kit, QueueType.Robot, 2, player.bukkit())
            Button.playNeutral(player.bukkit())

            player.sendMessage(
                "${CC.GREEN}Joining ${CC.PRI}Duos ${difficulty.lowercase().capitalize()} ${QueueType.Robot.name} ${kit.displayName}${CC.GREEN}..."
            )
        }.exceptionally {
            it.printStackTrace()
            player.sendMessage("${CC.RED}We weren't able to create a robot for you. Please report this to an administrator!")
            return@exceptionally null
        }
    }
}
