package gg.tropic.practice.games

import com.cryptomorin.xseries.XSound
import gg.scala.basics.plugin.profile.BasicsProfileService
import gg.scala.basics.plugin.settings.defaults.values.StateSettingValue
import gg.scala.commons.agnostic.sync.ServerSync
import gg.tropic.game.extensions.profile.CorePlayerProfileService
import gg.tropic.practice.commands.acquireProfileImmutable
import gg.tropic.practice.commands.networkTransaction
import gg.tropic.practice.expectation.ExpectationService
import gg.tropic.practice.expectation.GameExpectation
import gg.tropic.practice.feature.GameReportFeature
import gg.tropic.practice.games.bots.BotGameMetadata
import gg.tropic.practice.games.elo.ELOUpdates
import gg.tropic.practice.games.loadout.CustomLoadout
import gg.tropic.practice.games.loadout.DefaultLoadout
import gg.tropic.practice.games.loadout.SelectedLoadout
import gg.tropic.practice.games.ranked.PotPvPEloCalculator
import gg.tropic.practice.games.robot.RobotInstance
import gg.tropic.practice.games.tasks.GameStartTask
import gg.tropic.practice.games.tasks.GameStopTask
import gg.tropic.practice.games.team.GameTeam
import gg.tropic.practice.games.team.TeamIdentifier
import gg.tropic.practice.kit.Kit
import gg.tropic.practice.kit.feature.FeatureFlag
import gg.tropic.practice.kit.feature.GameLifecycle
import gg.tropic.practice.leaderboards.Reference
import gg.tropic.practice.leaderboards.ReferenceLeaderboardType
import gg.tropic.practice.leaderboards.ScoreUpdates
import gg.tropic.practice.map.BuiltMapReplication
import gg.tropic.practice.map.MapReplicationService
import gg.tropic.practice.map.MapService
import gg.tropic.practice.minigame.MiniGameLifecycle
import gg.tropic.practice.minigame.MiniGameOrchestrator
import gg.tropic.practice.minigame.MiniGameRegistry
import gg.tropic.practice.profile.PracticeProfileService
import gg.tropic.practice.queue.QueueType
import gg.tropic.practice.region.Region
import gg.tropic.practice.serializable.Message
import gg.tropic.practice.services.LeaderboardManagerService
import gg.tropic.practice.services.TournamentManagerService
import gg.tropic.practice.statistics.Counter
import gg.tropic.practice.utilities.BotMessages
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.terminable.composite.CompositeTerminable
import me.lucko.helper.utils.Players
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.FancyMessage
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.apache.commons.lang3.time.DurationFormatUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Vector
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Logger
import kotlin.math.roundToInt

/**
 * @author GrowlyX
 * @since 8/4/2022
 */
open class GameImpl(
    expectation: GameExpectation,
    kit: Kit,
    teams: Set<GameTeam> = expectation.teams,
    var state: GameState = GameState.Waiting,
    private val mapId: String = expectation.mapId
) : AbstractGame(expectation, teams, kit), CompositeTerminable by CompositeTerminable.create()
{
    val expectedSpectators = mutableSetOf<UUID>()
    var miniGameLifecycle: MiniGameLifecycle<*>? = null

    @Transient
    var currentGameStartCountdown = 5

    private val snapshots =
        mutableMapOf<UUID, GameReportSnapshot>()

    val map: gg.tropic.practice.map.Map
        get() = MapService.mapWithID(mapId)!!

    var replication: BuiltMapReplication? = null

    var arenaWorldName: String? = null

    val arenaWorld: World?
        get() = Bukkit.getWorld(this.arenaWorldName)

    private val audiences: BukkitAudiences
        get() = ExpectationService.audiences

    private fun durationMillis() =
        System.currentTimeMillis() - this.startTimestamp

    private val defaultLoadout: DefaultLoadout = DefaultLoadout(kit)
    var botGameMetadata: BotGameMetadata? = null

    private val selectedKitLoadouts = mutableMapOf<UUID, SelectedLoadout>()
    private val playerCounters = mutableMapOf<UUID, Counter>()

    val robotInstance = mutableSetOf<RobotInstance>()
    val consumedBots = AtomicInteger(0)

    val expectedQueueRejoin = mutableSetOf<UUID>()
    val placedBlocks = mutableSetOf<Vector>()

    var gameLifecycleObjectiveMet: (GameTeam) -> Boolean = { false }

    fun loadout(player: Player) = selectedKitLoadouts[player.uniqueId] ?: defaultLoadout

    fun takeSnapshot(player: Player)
    {
        this.snapshots[player.uniqueId] =
            GameReportSnapshot(player, counter(player), kit)
    }

    fun takeSnapshotIfNotAlreadyExists(player: Player)
    {
        if (this.snapshots.containsKey(player.uniqueId))
        {
            return
        }

        takeSnapshot(player)
    }

    fun sendInfoToTournamentService(losers: List<UUID>)
    {
        if (expectationModel.queueId != "tournament")
        {
            return
        }

        TournamentManagerService
            .publish(
                "game-completion",
                "losers" to losers
            )
    }

    private var endTimestamp: Long = 0L

    fun preWaitAdd(user: UUID, teamID: TeamIdentifier)
    {
        val firstEmpty = teams.firstOrNull { it.teamIdentifier == teamID }

        expectationModel.players += user
        val expectationTeam = expectationModel.teams
            .firstOrNull { it.teamIdentifier == teamID }
        expectationTeam?.players?.plusAssign(user)

        firstEmpty?.players?.plusAssign(user)
        expectationModel.players += user
    }

    fun preWaitRemove(player: Player)
    {
        val team = getTeamOf(player)
        expectationModel.players -= player.uniqueId

        val expectationTeam = expectationModel.teams
            .firstOrNull { it.teamIdentifier == team.teamIdentifier }

        expectationTeam?.players?.minusAssign(player.uniqueId)
        team.players.minusAssign(player.uniqueId)
    }

    fun complete(winner: GameTeam?, reason: String = "")
    {
        if (state == GameState.Completed)
        {
            return
        }

        this.state = GameState.Completed

        var eloUpdates: CompletableFuture<ELOUpdates>? = null
        val positionUpdates = mutableMapOf<UUID, CompletableFuture<ScoreUpdates>>()
        val extraInformation = mutableMapOf<UUID, Map<String, Map<String, String>>>()
        val playerFeedback = mutableMapOf<UUID, MutableList<String>>()

        if (robot())
        {
            robotInstance.forEach(RobotInstance::destroy)
        }

        toBukkitPlayers()
            .filterNotNull()
            .forEach(::takeSnapshotIfNotAlreadyExists)

        endTimestamp = System.currentTimeMillis()

        if (winner == null)
        {
            this.report = GameReport(
                identifier = UUID.randomUUID(),
                winners = listOf(), losers = listOf(),
                snapshots = snapshots,
                duration = this.durationMillis(),
                kit = this.kit.displayName,
                map = this.mapId,
                status = GameReportStatus.ForcefullyClosed,
                extraInformation = extraInformation
            )

            sendInfoToTournamentService(losers = listOf())
        } else
        {
            this.toBukkitPlayers()
                .filterNotNull()
                .onEach {
                    val profile = CorePlayerProfileService.find(it)
                    if (profile != null && expectationModel.queueType != null)
                    {
                        val userIsWinner = winner.players.contains(it.uniqueId)
                        val queueMultiplier = expectationModel.queueType!!.coinMultiplier
                        if (queueMultiplier != 0.0)
                        {
                            profile.addCoins(
                                coins = (queueMultiplier * (if (userIsWinner) 25 else 10)).toInt(),
                                reason = if (userIsWinner) "Winning a game" else "Playing a game",
                                feedback = { feedback ->
                                    playerFeedback.getOrPut(it.uniqueId) { mutableListOf() } += feedback
                                }
                            )
                        }
                    }
                }

            val opponents = getAllOpponents(winner)
            opponents.forEach {
                sendInfoToTournamentService(losers = it.players.toList())
            }

            val allOpponentPlayers = opponents.flatMap(GameTeam::players)
            if (!robot())
            {
                val kitStatisticsUpdates = allOpponentPlayers.map {
                    it.networkTransaction {
                        useKitStatistics(this@GameImpl) {
                            plays += 1

                            streakUpdates().apply(false)
                        }
                        globalStatistics.userPlayedGameAndLost().apply()
                    }
                } + winner.players.map {
                    it.networkTransaction {
                        useKitStatistics(this@GameImpl) {
                            wins += 1
                            plays += 1

                            streakUpdates().apply(true)
                        }
                        globalStatistics.userPlayedGameAndWon().apply()
                    }
                }

                val future = CompletableFuture.allOf(*kitStatisticsUpdates.toTypedArray())
                val firstOpponent = opponents.first()
                if (expectationModel.queueType == QueueType.Ranked && isTwoTeamGame() && firstOpponent.players.size == 1)
                {
                    val winnerProfileID = winner.players.firstOrNull()
                    val loserProfileID = firstOpponent.players.firstOrNull()

                    if (winnerProfileID != null && loserProfileID != null)
                    {
                        eloUpdates = CompletableFuture()

                        positionUpdates[winnerProfileID] = CompletableFuture()
                        positionUpdates[loserProfileID] = CompletableFuture()

                        future.thenRun {
                            val winnerProfile = winnerProfileID.acquireProfileImmutable().join()
                            val loserProfile = loserProfileID.acquireProfileImmutable().join()

                            val winnerRankedStats = winnerProfile.getRankedStatsFor(kit)
                            val winnerCurrentELO = winnerRankedStats.elo

                            val loserRankedStats = loserProfile.getRankedStatsFor(kit)
                            val loserCurrentELO = loserRankedStats.elo

                            val eloChanges = PotPvPEloCalculator.INSTANCE
                                .getNewRating(winnerCurrentELO, loserCurrentELO)

                            // winner elo network updates
                            winnerProfileID.networkTransaction {
                                getRankedStatsFor(kit).eloUpdates().apply(eloChanges.winnerNew)
                            }
                            loserProfileID.networkTransaction {
                                getRankedStatsFor(kit).eloUpdates().apply(eloChanges.loserNew)
                            }

                            val localELOUpdates = ELOUpdates(
                                winner = eloChanges.winnerNew to eloChanges.winnerGain,
                                loser = eloChanges.loserNew to eloChanges.loserGain
                            )

                            eloUpdates.complete(localELOUpdates)

                            LeaderboardManagerService
                                .updateScoreAndGetDiffs(
                                    winnerProfile.identifier,
                                    Reference(
                                        ReferenceLeaderboardType.ELO, kit.id
                                    ),
                                    newScore = eloChanges.winnerNew.toLong()
                                )
                                .thenAcceptAsync {
                                    positionUpdates[winnerProfile.identifier]?.complete(it)
                                }

                            LeaderboardManagerService
                                .updateScoreAndGetDiffs(
                                    loserProfile.identifier,
                                    Reference(
                                        ReferenceLeaderboardType.ELO, kit.id
                                    ),
                                    newScore = eloChanges.loserNew.toLong()
                                )
                                .thenAcceptAsync {
                                    positionUpdates[loserProfile.identifier]?.complete(it)
                                }
                        }
                    }
                }
            }

            toBukkitPlayers()
                .filterNotNull()
                .forEach {
                    counter(it).apply {
                        extraInformation[it.uniqueId] = mapOf(
                            "Hits" to mapOf(
                                "Total" to valueOf("totalHits").toInt().toString(),
                                "Max Combo" to valueOf("highestCombo").toInt().toString()
                            ),
                            "Other" to mapOf(
                                "Criticals" to valueOf("criticalHits").toInt().toString(),
                                "Blocked Hits" to valueOf("blockedHits").toInt().toString()
                            ),
                            "Health Regen" to mapOf(
                                "Regen" to "%.2f${Constants.HEART_SYMBOL}".format(
                                    valueOf("healthRegained").toFloat()
                                )
                            )
                        )
                    }
                }

            this.report = GameReport(
                identifier = UUID.randomUUID(),
                winners = winner.players.toList(),
                losers = allOpponentPlayers,
                snapshots = snapshots,
                duration = this.durationMillis(),
                map = this.mapId,
                kit = this.kit.displayName,
                status = GameReportStatus.Completed,
                extraInformation = extraInformation
            )

            if (robot())
            {
                val robot = robotInstance.firstOrNull() ?: return
                Schedulers
                    .async()
                    .runLater({
                        toBukkitPlayers()
                            .filterNotNull()
                            .forEach {
                                if (winner.players.contains(it.uniqueId))
                                {
                                    it.sendMessage(BotMessages.getBotLose(robot.name()))
                                } else
                                {
                                    if (it.uniqueId in allOpponentPlayers)
                                    {
                                        it.sendMessage(
                                            BotMessages.getBotWin(robot.name())
                                        )
                                    }
                                }

                                val profile = BasicsProfileService.find(it)
                                val messagingSoundsSettingValue = profile
                                    ?.setting(
                                        id = "messages_sounds",
                                        default = StateSettingValue.ENABLED
                                    )

                                if (messagingSoundsSettingValue == StateSettingValue.ENABLED)
                                {
                                    it.playSound(
                                        it.location,
                                        XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(),
                                        1F, 1F
                                    )
                                }
                            }
                    }, 50L)
            }
        }

        GameReportFeature.saveSnapshotForAllParticipants(report!!)

        val stopTask =
            GameStopTask(
                this, this.report!!, eloUpdates, positionUpdates,
                reason,
                playerFeedback = playerFeedback
            )

        stopTask.task = Schedulers.sync()
            .runRepeating(
                stopTask,
                0L, TimeUnit.SECONDS,
                1L, TimeUnit.SECONDS
            )

        stopTask.task.bindWith(this)
    }

    private fun initializeTeamObjectives()
    {
        if (flag(FeatureFlag.WinWhenNHitsReached))
        {
            gameLifecycleObjectiveMet = {
                val winWhenHitsReached = flagMetaData(
                    FeatureFlag.WinWhenNHitsReached, "hits"
                )
                    ?.toIntOrNull()

                winWhenHitsReached != null &&
                    it.gameLifecycleArbitraryObjectiveProgress >= winWhenHitsReached
            }
        } else
        {
            gameLifecycleObjectiveMet = {
                it.gameLifecycleArbitraryObjectiveProgress == 1
            }
        }
    }

    fun initializeAndStart()
    {
        initializeTeamObjectives()

        val startTask = GameStartTask(this)
        startTask.task = Schedulers.sync()
            .runRepeating(
                startTask,
                0L, TimeUnit.SECONDS,
                1L, TimeUnit.SECONDS
            )
        startTask.task.bindWith(this)
    }

    fun audiencesIndexed(lambda: (Audience, UUID) -> Unit) =
        this.toBukkitPlayers()
            .filterNotNull()
            .forEach {
                lambda(this.audiences.player(it), it.uniqueId)
            }

    fun audiences(lambda: (Audience) -> Unit) =
        this.toBukkitPlayers()
            .filterNotNull()
            .forEach {
                this.audiences.player(it).apply(lambda)
            }

    fun playSound(sound: Sound, pitch: Float = 1.0f)
    {
        this.toBukkitPlayers()
            .filterNotNull()
            .forEach {
                it.playSound(it.location, sound, 1.0f, pitch)
            }
    }

    fun allNonSpectators() = teams.flatMap { it.nonSpectators() }

    fun sendMessage(vararg message: String)
    {
        this.toBukkitPlayers()
            .filterNotNull()
            .forEach {
                message.forEach { msg ->
                    it.sendMessage(msg)
                }
            }

        this.expectedSpectators
            .mapNotNull(Bukkit::getPlayer)
            .forEach {
                message.forEach { msg ->
                    it.sendMessage(msg)
                }
            }
    }

    fun sendMessage(vararg message: Message)
    {
        this.toBukkitPlayers()
            .filterNotNull()
            .forEach {
                message.forEach { msg ->
                    msg.sendToPlayer(it)
                }
            }

        this.expectedSpectators
            .mapNotNull(Bukkit::getPlayer)
            .forEach {
                message.forEach { msg ->
                    msg.sendToPlayer(it)
                }
            }
    }

    fun sendMessage(vararg message: FancyMessage)
    {
        this.toBukkitPlayers()
            .filterNotNull()
            .forEach {
                message.forEach { msg ->
                    msg.sendToPlayer(it)
                }
            }

        this.expectedSpectators
            .mapNotNull(Bukkit::getPlayer)
            .forEach {
                message.forEach { msg ->
                    msg.sendToPlayer(it)
                }
            }
    }

    fun getTeamOf(player: Player) = this.teams
        .first {
            player.uniqueId in it.players
        }

    fun getNullableTeam(player: Player) = this.teams
        .firstOrNull {
            player.uniqueId in it.players
        }

    fun isTwoTeamGame() = teams.size == 2

    fun getOpponent(player: Player) =
        this.getOpponent(
            this.getTeamOf(player)
        )

    fun getOpponent(team: GameTeam) = if (team.teamIdentifier != TeamIdentifier.A)
        teams.first() else teams.last()

    fun getAllOpponents(team: GameTeam) = teams.filterNot { it.teamIdentifier == team.teamIdentifier }

    fun getDuration(): String
    {
        if (
            !this.ensurePlaying() &&
            !this.state(GameState.Completed)
        )
        {
            // This will return a capitalized name state which
            // makes sense in the context this function will be used in.
            return this.state.name
        }

        if (state(GameState.Completed))
        {
            return DurationFormatUtils.formatDuration(
                endTimestamp - startTimestamp,
                "mm:ss"
            )
        }

        return DurationFormatUtils.formatDuration(
            System.currentTimeMillis() - this.startTimestamp, "mm:ss"
        )
    }

    fun toBukkitPlayers() = this.teams
        .flatMap {
            it.toBukkitPlayers()
        }

    fun toPlayers() = this.teams
        .flatMap {
            it.players
        }

    fun generateRedirectMetadataFor(player: Player): Map<String, String>
    {
        if (
            player.uniqueId in expectedSpectators ||
            player.uniqueId !in expectedQueueRejoin
        )
        {
            if (expectationModel.players.size != 2)
            {
                if (player.uniqueId in expectationModel.players)
                {
                    return mapOf("was-game-participant" to "true")
                }

                return mapOf()
            }

            if (player.uniqueId in expectationModel.players)
            {
                val target = expectationModel.players
                    .firstOrNull { other ->
                        player.uniqueId != other
                    }
                    ?: return mapOf()

                if (expectationModel.queueId == "party" || expectationModel.queueId == "tournament")
                {
                    return mapOf("was-game-participant" to "true")
                }

                return mapOf(
                    "rematch-target-id" to target.toString(),
                    "rematch-kit-id" to expectationModel.kitId,
                    "rematch-region" to Region
                        .extractFrom(
                            ServerSync.getLocalGameServer().id
                        )
                        .name,
                    "rematch-map-id" to expectationModel.mapId,
                    "was-game-participant" to "true"
                )
            }

            return mapOf()
        }

        val queueType = expectationModel.queueType?.name
            ?: return mapOf()

        return mapOf(
            "requeue-kit-id" to expectationModel.kitId,
            "requeue-queue-type" to queueType,
            "was-game-participant" to "true"
        )
    }

    fun closeAndCleanup(kickPlayers: Boolean = true)
    {
        if (state != GameState.Completed)
        {
            Logger.getAnonymousLogger().info(
                "Game tried to close prematurely"
            )
            return
        }

        fun getOnlinePlayers() = Players.all()
            .filter {
                it.location.world.name == arenaWorldName
            }
            .filterNotNull()
            .toTypedArray()

        if (kickPlayers)
        {
            val onlinePlayers = getOnlinePlayers()

            if (onlinePlayers.isNotEmpty())
            {
                GameService.redirector.redirect(
                    ::generateRedirectMetadataFor,
                    *onlinePlayers
                )
            }
        }

        GameService.gameMappings.remove(this.expectation)

        Tasks.delayed(20L) {
            if (arenaWorld == null)
            {
                return@delayed
            }

            MapReplicationService.removeReplicationMatchingWorld(arenaWorld!!)

            robotInstance.forEach(RobotInstance::destroy)
            robotInstance.clear()

            val handlePlayers = (arenaWorld!! as CraftWorld).handle

            if (kickPlayers)
            {
                // final check
                val onlinePlayers = handlePlayers.players
                if (onlinePlayers.isNotEmpty())
                {
                    onlinePlayers.toList().forEach {
                        if (it.bukkitEntity !is Player)
                        {
                            return@forEach
                        }

                        val player = it.bukkitEntity as Player
                        runCatching {
                            player.kickPlayer("You should not be on this server!")
                        }

                        onlinePlayers.remove(it)
                    }

                    Tasks.delayed(10L) {
                        Bukkit.unloadWorld(arenaWorldName, false)
                    }
                    return@delayed
                }
            }

            MapReplicationService.mapReplications -= replication
            Bukkit.unloadWorld(arenaWorldName, false)
        }

        closeAndReportException()
    }

    fun ensurePlaying() = this.state(GameState.Playing)

    fun state(state: GameState): Boolean
    {
        return this.state == state
    }

    fun flag(flag: FeatureFlag) = this.kit.features[flag] != null

    fun flagMetaData(flag: FeatureFlag, key: String): String?
    {
        if (!this.kit.features(flag))
        {
            return null
        }

        return this.kit
            .features[flag]
            ?.get(key)
            ?: flag.schema[key]
    }

    private val loadoutSelection = mutableMapOf<UUID, CompositeTerminable>()
    fun completeLoadoutSelection()
    {
        loadoutSelection.values.forEach(
            CompositeTerminable::closeAndReportException
        )
        loadoutSelection.clear()
    }

    fun enterLoadoutSelection(player: Player)
    {
        val profile = PracticeProfileService
            .find(player)
            ?: return run {
                defaultLoadout.apply(player)
            }

        val loadouts = profile.customLoadouts
            .getOrDefault(kit.id, listOf())

        if (loadouts.isEmpty())
        {
            defaultLoadout.apply(player)
            return
        }

        val defaultLoadoutID = UUID.randomUUID()

        val terminable = CompositeTerminable.create()
        terminable.with {
            if (!selectedKitLoadouts.containsKey(player.uniqueId))
            {
                defaultLoadout.apply(player)
                selectedKitLoadouts[player.uniqueId] = defaultLoadout
            }
        }

        val applicableLoadouts = loadouts
            .map {
                @Suppress("USELESS_CAST")
                CustomLoadout(it, kit) as SelectedLoadout
            }
            .associateBy {
                UUID.randomUUID().toString()
            }
            .toMutableMap()
            .apply {
                this[defaultLoadoutID.toString()] = defaultLoadout
            }

        val hashCodeToItemMappings = mutableMapOf<Int, String>()
        Events
            .subscribe(PlayerInteractEvent::class.java)
            .filter {
                it.player.uniqueId == player.uniqueId
            }
            .filter {
                it.action == Action.RIGHT_CLICK_AIR
            }
            .filter {
                hashCodeToItemMappings.containsKey(it.item.hashCode())
            }
            .handler {
                val loadout = applicableLoadouts[hashCodeToItemMappings[it.item.hashCode()]]
                    ?: defaultLoadout

                selectedKitLoadouts[player.uniqueId] = loadout

                terminable.closeAndReportException()
                loadoutSelection.remove(player.uniqueId)

                loadout.apply(player)

                player.sendMessage(
                    "${CC.GREEN}You have selected the ${CC.WHITE}${loadout.displayName()}${CC.GREEN} loadout!"
                )
            }
            .bindWith(terminable)

        applicableLoadouts.forEach { (t, u) ->
            val item = ItemBuilder
                .of(
                    if (t == defaultLoadoutID.toString())
                        Material.BOOK else Material.ENCHANTED_BOOK
                )
                .name("${CC.GREEN}${u.displayName()} ${CC.GRAY}(Right-Click)")
                .addToLore(
                    "${CC.GRAY}Click to select this loadout."
                )
                .build()

            hashCodeToItemMappings[item.hashCode()] = t
            player.inventory.addItem(item)
        }

        loadoutSelection[player.uniqueId] = terminable
    }

    fun lifecycle() = kit.lifecycle()

    fun counter(player: Player) = playerCounters.getOrPut(player.uniqueId) { Counter(player.uniqueId) }
    open fun buildResources(): Boolean
    {
        toPlayers().forEach {
            playerCounters[it] = Counter(it)
        }
        return true
    }

    fun cleanupWorld()
    {
        if (placedBlocks.isNotEmpty())
        {
            placedBlocks.forEach {
                arenaWorld
                    ?.getBlockAt(it.toLocation(arenaWorld))
                    ?.apply {
                        type = Material.AIR
                    }
            }
        }

        arenaWorld?.entities
            ?.filterIsInstance<Item>()
            ?.onEach {
                it.remove()
            }
    }

    fun buildLimit(): Int?
    {
        val buildLimit = flagMetaData(
            FeatureFlag.BuildLimit,
            key = "blocks"
        )?.toIntOrNull()
            ?: return null

        return (map.findSpawnLocationMatchingTeam(TeamIdentifier.A)?.y?.roundToInt() ?: 0) + buildLimit
    }

    fun robot(solaraID: UUID) = robotInstance.firstOrNull { it.solaraID() == solaraID }

    fun robot(): Boolean
    {
        return expectationModel.queueType == QueueType.Robot
    }

    fun humanSide() = teams.first()
    fun robotSide() = teams.last()
}
