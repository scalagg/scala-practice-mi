package gg.tropic.practice.menu.party

import gg.tropic.practice.party.WParty
import gg.tropic.practice.player.LobbyPlayerService
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 2/9/2024
 */
class PartyPlayGameSelectMenu() : Menu("Select a game")
{
    init
    {
        placeholder = true
    }

    override fun getButtons(player: Player) = mapOf(
        5 to ItemBuilder
            .of(Material.GOLD_SWORD)
            .name("${CC.AQUA}Team vs. Team Fights")
            .addToLore(
                "${CC.WHITE}Create two teams with your",
                "${CC.WHITE}party members and fight",
                "${CC.WHITE}against one another!",
                "",
                "${CC.GREEN}Click to play!"
            )
            .toButton { _, _ ->
                val lobbyPlayer = LobbyPlayerService.find(player)
                    ?: return@toButton

                player.closeInventory()
                if (!lobbyPlayer.isInParty())
                {
                    player.sendMessage("${CC.RED}You are no longer in a party!")
                    return@toButton
                }

                lobbyPlayer.partyOf()
                    .onlinePracticePlayersInLobby()
                    .thenAccept {
                        if (it.keys.size < 2)
                        {
                            player.sendMessage("${CC.RED}You must have at least two players in your party to start a Team vs. Team fight!")
                            return@thenAccept
                        }

                        PartyPlayTVTFights(it.keys.toList()).openMenu(player)
                    }
            },
        5 to ItemBuilder
            .of(Material.ENDER_PORTAL_FRAME)
            .name("${CC.GOLD}Robot Fights")
            .addToLore(
                "${CC.WHITE}Your team versus bots!",
                "",
                "${CC.GREEN}Click to prepare!"
            )
            .toButton { _, _ ->
                val lobbyPlayer = LobbyPlayerService.find(player)
                    ?: return@toButton

                player.closeInventory()
                if (!lobbyPlayer.isInParty())
                {
                    player.sendMessage("${CC.RED}You are no longer in a party!")
                    return@toButton
                }

                lobbyPlayer.partyOf()
                    .onlinePracticePlayersInLobby()
                    .thenAccept {
                        if (it.keys.size < 2)
                        {
                            player.sendMessage("${CC.RED}You must have at least two players in your party to start a Team vs. Team fight!")
                            return@thenAccept
                        }

                        PartyPlayBotsIntegration.createBotsWith(player, it.keys)
                    }
            }
    )
}
