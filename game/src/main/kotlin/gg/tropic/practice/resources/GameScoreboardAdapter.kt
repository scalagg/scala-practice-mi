package gg.tropic.practice.resources

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.LemonConstants
import gg.scala.lemon.util.QuickAccess.username
import gg.tropic.practice.Globals
import gg.tropic.practice.games.GameService
import gg.tropic.practice.games.GameState
import gg.tropic.practice.games.team.GameTeamSide
import gg.tropic.practice.kit.feature.FeatureFlag
import gg.tropic.practice.kit.feature.GameLifecycle
import gg.tropic.practice.region.PlayerRegionFromRedisProxy
import gg.tropic.practice.services.ScoreboardTitleService
import gg.tropic.practice.settings.layout
import gg.tropic.practice.settings.scoreboard.ScoreboardStyle
import gg.tropic.practice.utilities.colorOf
import gg.tropic.practice.utilities.toRBTeamSide
import me.lucko.helper.Events
import me.lucko.helper.Helper
import net.evilblock.cubed.scoreboard.ScoreboardAdapter
import net.evilblock.cubed.scoreboard.ScoreboardAdapterRegister
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.nms.MinecraftReflection
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

/**
 * @author GrowlyX
 * @since 8/5/2022
 */
@Service
@ScoreboardAdapterRegister
object GameScoreboardAdapter : ScoreboardAdapter()
{
    override fun getLines(
        board: LinkedList<String>, player: Player
    )
    {
        val layout: ScoreboardStyle = layout(player)

        if (layout == ScoreboardStyle.Disabled)
        {
            return
        }

        val game = GameService
            .byPlayerOrSpectator(player.uniqueId)
            ?: return

        board += ""

        fun GameTeamSide.format() = if (game.flag(FeatureFlag.RedBlueTeams)) toRBTeamSide().display[0] else name

        fun insertMRStatusSection(firstPerson: Boolean = true)
        {
            val self = if (firstPerson) game.getTeamOf(player) else game.teams[GameTeamSide.A]!!
            val selfColor = game.colorOf(self)

            val them = game.getOpponent(self)!!
            val theirColor = game.colorOf(them)

            val teamSection = mutableListOf<String>()
            teamSection += "${CC.PRI}Teams: "

            when (game.lifecycle())
            {
                GameLifecycle.SoulBound ->
                {
                }

                GameLifecycle.RoundBound ->
                {
                    if (game.flag(FeatureFlag.RedBlueTeams))
                    {
                        val red = game.teams[GameTeamSide.A]!!
                        val blue = game.teams[GameTeamSide.B]!!

                        val roundsRequiredForCompletion = game
                            .flagMetaData(FeatureFlag.RoundsRequiredToCompleteGame, "value")
                            ?.toIntOrNull() ?: 2

                        var ballID = ""
                        repeat(red.gameLifecycleArbitraryObjectiveProgress)
                        {
                            ballID += "${CC.RED}⬤"
                        }

                        repeat(roundsRequiredForCompletion - red.gameLifecycleArbitraryObjectiveProgress)
                        {
                            ballID += "${CC.GRAY}⬤"
                        }

                        teamSection += "${CC.B_RED}R ${CC.WHITE}Red: ${CC.RED}$ballID${
                            if (red.side == self.side && firstPerson) " ${CC.GRAY}YOU" else ""
                        }"

                        ballID = ""
                        repeat(blue.gameLifecycleArbitraryObjectiveProgress)
                        {
                            ballID += "${CC.BLUE}⬤"
                        }

                        repeat(roundsRequiredForCompletion - blue.gameLifecycleArbitraryObjectiveProgress)
                        {
                            ballID += "${CC.GRAY}⬤"
                        }

                        teamSection += "${CC.B_BLUE}R ${CC.WHITE}Blue: ${CC.BLUE}$ballID${
                            if (red.side == self.side && firstPerson) " ${CC.GRAY}YOU" else ""
                        }"
                    } else
                    {
                        val roundsRequiredForCompletion = game
                            .flagMetaData(FeatureFlag.RoundsRequiredToCompleteGame, "value")
                            ?.toIntOrNull() ?: 2

                        var ballID = ""
                        repeat(self.gameLifecycleArbitraryObjectiveProgress)
                        {
                            ballID += "$selfColor⬤"
                        }

                        repeat(roundsRequiredForCompletion - self.gameLifecycleArbitraryObjectiveProgress)
                        {
                            ballID += "${CC.GRAY}⬤"
                        }

                        teamSection += "${CC.WHITE}${if (!firstPerson) self.side.format() else "You"}: $ballID"

                        ballID = ""
                        repeat(them.gameLifecycleArbitraryObjectiveProgress)
                        {
                            ballID += "$theirColor⬤"
                        }

                        repeat(roundsRequiredForCompletion - them.gameLifecycleArbitraryObjectiveProgress)
                        {
                            ballID += "${CC.GRAY}⬤"
                        }

                        teamSection += "${CC.WHITE}${if (!firstPerson) them.side.format() else "Them"}: $ballID"
                    }
                }

                GameLifecycle.ObjectiveBound ->
                {
                    if (game.flag(FeatureFlag.RedBlueTeams))
                    {
                        val red = game.teams[GameTeamSide.A]!!
                        val blue = game.teams[GameTeamSide.B]!!
                        teamSection += "${CC.B_RED}R ${CC.WHITE}Red: ${CC.RED}${
                            red.gameLifecycleArbitraryObjectiveProgress
                        }${
                            if (red.side == self.side && firstPerson) " ${CC.GRAY}YOU" else ""
                        }"
                        teamSection += "${CC.B_BLUE}B ${CC.WHITE}Blue: $theirColor${
                            blue.gameLifecycleArbitraryObjectiveProgress
                        }${
                            if (blue.side == self.side && firstPerson) " ${CC.GRAY}YOU" else ""
                        }"
                    } else
                    {
                        teamSection += "${CC.GRAY}${if (!firstPerson) self.side.format() else "You"}: $selfColor${
                            self.gameLifecycleArbitraryObjectiveProgress
                        }"
                        teamSection += "${CC.GRAY}${if (!firstPerson) them.side.format() else "Them"}: $theirColor${
                            them.gameLifecycleArbitraryObjectiveProgress
                        }"
                    }
                }

                GameLifecycle.ObjectivePlusSoulBound ->
                {
                    if (game.flag(FeatureFlag.RedBlueTeams))
                    {
                        val red = game.teams[GameTeamSide.A]!!
                        val blue = game.teams[GameTeamSide.B]!!
                        teamSection += "${CC.B_RED}R ${CC.WHITE}Red: ${CC.RED}${
                            if (game.gameLifecycleObjectiveMet(red)) "${red.nonSpectators().size}" else "✔"
                        }${
                            if (red.side == self.side && firstPerson) " ${CC.GRAY}YOU" else ""
                        }"
                        teamSection += "${CC.B_BLUE}B ${CC.WHITE}Blue: $theirColor${
                            if (game.gameLifecycleObjectiveMet(blue)) "${blue.nonSpectators().size}" else "✔"
                        }${
                            if (blue.side == self.side && firstPerson) " ${CC.GRAY}YOU" else ""
                        }"
                    } else
                    {
                        teamSection += "${CC.GRAY}You: $selfColor${
                            if (game.gameLifecycleObjectiveMet(them)) "${self.nonSpectators().size}" else "✔"
                        }"
                        teamSection += "${CC.GRAY}Them: $theirColor${
                            if (game.gameLifecycleObjectiveMet(self)) "${them.nonSpectators().size}" else "✔"
                        }"
                    }
                }
            }

            teamSection += ""
            board.addAll(1, teamSection)
        }

        if (player.uniqueId in game.expectedSpectators)
        {
            for (team in (if (game.robot()) listOf(game.humanSide()) else game.teams.values))
            {
                fun Player.format() = "${CC.WHITE}${
                    if (hasMetadata("spectator")) CC.STRIKE_THROUGH else ""
                }$name ${CC.GRAY}(${
                    MinecraftReflection.getPing(this)
                }ms)"

                val bukkitPlayers = team.toBukkitPlayers().filterNotNull()
                val sidePrefix = "${game.colorOf(team)}[${team.side.format()}]:"

                board += "$sidePrefix ${
                    if (bukkitPlayers.size == 1) bukkitPlayers.first().format() else ""
                }"

                if (bukkitPlayers.size > 1)
                {
                    bukkitPlayers
                        .take(2)
                        .forEach {
                            board += "- ${it.format()}"
                        }

                    board += ""
                }
            }

            if (board.last() != "")
            {
                board += ""
            }

            board += "${CC.GRAY}Map: ${CC.PRI}${game.map.displayName}"
            board += "${CC.GRAY}Type: ${CC.PRI}${game.expectationModel.queueType ?: "Duel"}"
            board += "${CC.GRAY}Duration: ${CC.PRI}${game.getDuration()}"

            if (game.lifecycle()!= GameLifecycle.SoulBound)
            {
                insertMRStatusSection(firstPerson = false)
            }
        } else
        {
            when (game.state)
            {
                GameState.Waiting ->
                {
                    board += "${CC.GRAY}Waiting for players..."
                }

                GameState.Starting ->
                {
                    val opponent = game.getOpponent(player)
                        ?: return

                    board += "${CC.GRAY}Starting in: ${CC.PRI}${game.activeCountdown}s"

                    if (opponent.players.size == 1)
                    {
                        board += "${CC.GRAY}Opponent: ${CC.PRI}${
                            if (game.robot()) game.robotInstance?.name() ?: "Robot" else opponent.players.first().username()
                        }"
                    } else if (!game.robot())
                    {
                        board += "${CC.PRI}Opponents:"

                        for (other in opponent.players.take(3))
                        {
                            board += "${CC.GRAY} - ${CC.WHITE}${other.username()}"
                        }

                        if (opponent.players.size > 3)
                        {
                            board += "${CC.WHITE}(and ${opponent.players.size - 3} more...)"
                        }
                    }

                    board += ""
                    board += "${CC.PRI}Ping:"
                    board += "${CC.GRAY}You: ${CC.GREEN}${MinecraftReflection.getPing(player)}ms"

                    if (!game.robot())
                    {
                        val opponentPlayer = opponent.players.first()
                        board += "${CC.GRAY}Them: ${CC.RED}${
                            if (Bukkit.getPlayer(opponentPlayer) != null)
                                MinecraftReflection.getPing(
                                    Bukkit.getPlayer(opponentPlayer)
                                ) else "0"
                        }ms"
                    }
                }

                GameState.Playing ->
                {
                    val opponent = game.getOpponent(player)
                        ?: return
                    val showHitScoreboard = game.flag(FeatureFlag.WinWhenNHitsReached)
                    if (opponent.players.size == 1)
                    {
                        val opponentPlayer = opponent.players.first()
                        if (showHitScoreboard)
                        {
                            val teamOfPlayer = game.getTeamOf(player)
                            val teamOfOpponent = game.getOpponent(player)!!
                            val hitsDiff =
                                teamOfPlayer.gameLifecycleArbitraryObjectiveProgress - teamOfOpponent.gameLifecycleArbitraryObjectiveProgress

                            val playerCombo = teamOfPlayer.playerCombos[player.uniqueId] ?: 0

                            board += "${CC.PRI}Hits:"
                            board += "${CC.GRAY}You: ${CC.GREEN}${
                                teamOfPlayer.gameLifecycleArbitraryObjectiveProgress
                            }${
                                if (hitsDiff == 0)
                                    ""
                                else
                                    if (hitsDiff > 0)
                                        " ${CC.GREEN}(+$hitsDiff)"
                                    else
                                        " ${CC.RED}($hitsDiff)"
                            }"
                            board += " ${
                                if (playerCombo == 0)
                                    "${CC.D_GRAY}No combo"
                                else
                                    if (playerCombo > 0)
                                        "${CC.GREEN}+$playerCombo combo"
                                    else
                                        ""
                            }"

                            board += "${CC.GRAY}Them: ${CC.RED}${
                                teamOfOpponent.gameLifecycleArbitraryObjectiveProgress
                            }"
                        } else
                        {
                            if (game.lifecycle() != GameLifecycle.SoulBound)
                            {
                                insertMRStatusSection(firstPerson = true)
                            }

                            board += "${CC.PRI}Ping:"
                            board += "${CC.GRAY}You: ${CC.GREEN}${MinecraftReflection.getPing(player)}ms"
                            if (!game.robot())
                            {
                                board += "${CC.GRAY}Them: ${CC.RED}${
                                    if (Bukkit.getPlayer(opponentPlayer) != null)
                                        MinecraftReflection.getPing(
                                            Bukkit.getPlayer(opponentPlayer)
                                        ) else "0"
                                }ms"
                            }
                        }

                        board += ""
                        board += "${CC.GRAY}Opponent: ${CC.PRI}${
                            if (game.robot()) game.robotInstance?.name() ?: "Robot" else opponentPlayer.username()
                        }"
                        board += "${CC.GRAY}Duration: ${CC.PRI}${game.getDuration()}"
                    } else if (!game.robot())
                    {
                        if (game.lifecycle() != GameLifecycle.SoulBound)
                        {
                            insertMRStatusSection(firstPerson = true)
                        }

                        if (!showHitScoreboard)
                        {
                            board += "${CC.PRI}Ping:"
                            board += "${CC.GRAY}You: ${CC.GREEN}${MinecraftReflection.getPing(player)}ms"
                            board += "${CC.GRAY}Them:"

                            for (other in opponent.players.take(3))
                            {
                                val bukkitPlayer = Bukkit.getPlayer(other)
                                    ?: continue

                                board += "${CC.GRAY} - ${CC.WHITE}${
                                    if (bukkitPlayer.hasMetadata("spectator")) CC.STRIKE_THROUGH else ""
                                }${other.username()} ${CC.D_GRAY}(${
                                    MinecraftReflection.getPing(bukkitPlayer)
                                }ms)"
                            }

                            if (opponent.players.size > 3)
                            {
                                board += "${CC.WHITE}(and ${opponent.players.size - 3} more...)"
                            }
                        } else
                        {
                            val teamOfPlayer = game.getTeamOf(player)
                            val teamOfOpponent = game.getOpponent(player)!!
                            val hitsDiff =
                                teamOfPlayer.gameLifecycleArbitraryObjectiveProgress - teamOfOpponent.gameLifecycleArbitraryObjectiveProgress

                            board += "${CC.PRI}Hits:"
                            board += "${CC.GRAY}Team: ${CC.PRI}${
                                teamOfPlayer.gameLifecycleArbitraryObjectiveProgress
                            }${
                                if (hitsDiff == 0)
                                    ""
                                else
                                    if (hitsDiff > 0)
                                        " ${CC.GREEN}(+$hitsDiff)"
                                    else
                                        " ${CC.RED}($hitsDiff)"
                            }"

                            board += "${CC.GRAY}Opponent: ${CC.RED}${teamOfOpponent.gameLifecycleArbitraryObjectiveProgress}"

                            for (other in opponent.players.take(3))
                            {
                                val bukkitPlayer = Bukkit.getPlayer(other)
                                    ?: continue

                                board += "${CC.GRAY} - ${CC.WHITE}${
                                    if (bukkitPlayer.hasMetadata("spectator")) CC.STRIKE_THROUGH else ""
                                }${other.username()} ${CC.D_GRAY}(${
                                    MinecraftReflection.getPing(bukkitPlayer)
                                }ms)"
                            }

                            if (opponent.players.size > 3)
                            {
                                board += "${CC.WHITE}(and ${opponent.players.size - 3} more...)"
                            }
                        }

                        board += ""
                        board += "${CC.GRAY}Map: ${CC.PRI}${game.map.displayName}"
                        board += "${CC.GRAY}Duration: ${CC.PRI}${game.getDuration()}"
                    }
                }

                GameState.Completed ->
                {
                    val report = game.report

                    if (report != null)
                    {
                        val robotID = Globals.PLAYER_BOT_UNIQUE_ID
                        fun List<UUID>.toReport(): String
                        {
                            val firstUsername = if (isNotEmpty()) (if (robotID == first()) game.robotInstance?.name() ?: "Robot" else first().username()) else null

                            return when (size)
                            {
                                0 -> "N/A"
                                1 -> firstUsername + CC.GRAY + (Bukkit.getPlayer(report.winners.first())?.let { " (" + MinecraftReflection.getPing(it) + "ms)" } ?: "")
                                else -> "$firstUsername's Team"
                            }
                        }

                        board += "${CC.GRAY}Winner:"
                        board += " ${CC.GREEN}${report.winners.toReport()}"
                        board += "${CC.GRAY}Loser:"
                        board += " ${CC.RED}${report.losers.toReport()}"
                        board += ""
                        board += "${CC.GRAY}Duration: ${CC.PRI}${game.getDuration()}"
                        board += "${CC.GRAY}Kit: ${CC.PRI}${report.kit}"
                    } else
                    {
                        board += "${CC.D_GRAY}Loading report..."
                    }
                }
            }
        }

        board += ""
        board += "${CC.YELLOW}solara.gg${CC.GRAY}      ${CC.YELLOW}"
    }

    @Configure
    fun configure()
    {
        Events
            .subscribe(PlayerJoinEvent::class.java)
            .handler {
                PlayerRegionFromRedisProxy.of(it.player)
                    .thenAccept { region ->
                        it.player.setMetadata(
                            "region",
                            FixedMetadataValue(Helper.hostPlugin(), region.name)
                        )
                    }
            }

        Events
            .subscribe(PlayerQuitEvent::class.java)
            .handler {
                it.player.removeMetadata("region", Helper.hostPlugin())
            }
    }

    override fun getTitle(player: Player) = ScoreboardTitleService.getCurrentTitle()
}
