package gg.tropic.practice.services

import gg.scala.aware.AwareBuilder
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.tropic.practice.games.GameService
import gg.tropic.practice.games.GameState
import gg.tropic.practice.games.matchmaking.JoinIntoGameResult
import gg.tropic.practice.games.matchmaking.JoinIntoGameStatus
import gg.tropic.practice.games.spectate.SpectateRequest
import gg.tropic.practice.suffixWhenDev
import net.evilblock.cubed.serializers.Serializers
import java.util.*
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 10/20/2023
 */
@Service
object RequestService
{
    private val aware by lazy {
        AwareBuilder
            .of<AwareMessage>("practice:queue-inhabitants".suffixWhenDev())
            .codec(AwareMessageCodec)
            .logger(Logger.getAnonymousLogger())
            .build()
    }

    private fun createMessage(packet: String, vararg pairs: Pair<String, Any?>): AwareMessage =
        AwareMessage.of(packet, aware, *pairs)

    @Configure
    fun configure()
    {
        aware.listen("request-spectate") {
            val server = retrieve<String>("server")
            if (ServerSync.local.id != server)
            {
                return@listen
            }

            val request = retrieve<SpectateRequest>("request")
            val game = retrieve<UUID>("game")
            val requestID = retrieve<UUID>("requestID")

            val gameImpl = GameService.gameMappings[game]
                ?: return@listen

            gameImpl.expectedSpectators += request.player

            createMessage(
                "spectate-ready",
                "requestID" to requestID
            ).publish(
                AwareThreadContext.SYNC,
                "practice:queue".suffixWhenDev()
            )
        }

        aware.listen("request-join-into") {
            val game = retrieve<UUID>("game")
            val gameImpl = GameService.gameMappings[game]
                ?: return@listen

            val player = retrieve<UUID>("player")
            val requestID = retrieve<UUID>("requestID")

            var status = JoinIntoGameStatus.Success
            if (gameImpl.state(GameState.Waiting))
            {
                status = JoinIntoGameStatus.FailureStarted
            } else
            {
                val emptyTeams = gameImpl.teams.filter { it.players.isEmpty() }
                if (emptyTeams.isEmpty())
                {
                    status = JoinIntoGameStatus.FailureNonEmptyTeams
                } else
                {
                    val firstEmpty = emptyTeams.first()
                    val teamID = firstEmpty.teamIdentifier

                    val expectationTeam = gameImpl.expectationModel.teams
                        .firstOrNull { it.teamIdentifier == teamID }
                    expectationTeam?.players?.plusAssign(player)

                    firstEmpty.players += player
                }
            }

            createMessage(
                "join-into-ready",
                "requestID" to requestID,
                "result" to Serializers.gson.toJson(JoinIntoGameResult(
                    status = status
                ))
            ).publish(
                AwareThreadContext.SYNC,
                "practice:queue".suffixWhenDev()
            )
        }
        aware.connect().toCompletableFuture().join()
    }
}
