package gg.scala.practice.party

import gg.scala.lemon.util.QuickAccess
import gg.scala.parties.model.Party
import gg.scala.practice.lobbyGroup
import gg.scala.practice.suffixWhenDev
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 2/9/2024
 */
data class WParty(var delegate: Party)
{
    var currentPlayers = 0
    fun update(party: Party)
    {
        this.delegate = party
    }

    fun isInParty() = delegate
    fun onlinePracticePlayersInLobby() = CompletableFuture.supplyAsync {
        delegate.includedMembers()
            .associateWith { QuickAccess.server(it).join() }
            .filter {
                it.value?.groups
                    ?.contains(lobbyGroup().suffixWhenDev()) == true
            }
    }
}
