package gg.scala.practice.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.Conditions
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.practice.configuration.PracticeConfigurationService
import gg.scala.practice.player.LobbyPlayerService
import gg.scala.practice.player.hotbar.LobbyHotbarService
import org.bukkit.Bukkit

/**
 * @author GrowlyX
 * @since 10/19/2023
 */
@AutoRegister
object SpawnCommand : ScalaCommand()
{
    @CommandAlias("spawn")
    fun onSpawn(
        @Conditions("cooldown:duration=2,unit=SECONDS")
        player: ScalaPlayer
    )
    {
        with(PracticeConfigurationService.cached()) {
            player.teleport(
                spawnLocation
                    .toLocation(
                        Bukkit.getWorlds().first()
                    )
            )

            LobbyPlayerService.find(player.bukkit())
                ?.apply {
                    LobbyHotbarService
                        .get(state)
                        .applyToPlayer(player.bukkit())
                }
        }
    }

    @CommandAlias("get-lobby-items")
    fun onLobby(
        player: ScalaPlayer
    )
    {
        LobbyPlayerService.find(player.bukkit())
            ?.apply {
                LobbyHotbarService
                    .get(state)
                    .applyToPlayer(player.bukkit())
            }
    }
}
