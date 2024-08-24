package gg.tropic.practice.tablist

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.agnostic.sync.server.ServerContainer
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.tropic.practice.namespace
import gg.tropic.practice.utilities.getAs
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.protocol.Protocol
import me.lucko.helper.utils.Players
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.scoreboard.ScoreboardTeamPacketMod
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

/**
 * @author GrowlyX
 * @since 8/18/2024
 */
@Service
object RedisTabSync
{
    val remoteTrackedPlayers = mutableMapOf<UUID, ImmutableTrackedPlayer>()
    val localReceivingPlayers = mutableSetOf<UUID>()
    val updateLock = ReentrantLock()

    @Inject
    lateinit var plugin: ExtendedScalaPlugin

    @Configure
    fun configure()
    {
        Schedulers
            .async()
            .runRepeating({ _ ->
                val localTrackedPlayers = Players.all()
                    .filter { !it.hasMetadata("vanished") }
                    .map(ImmutableTrackedPlayer::of)

                ScalaCommonsSpigot.instance
                    .kvConnection.sync()
                    .setex(
                        "${namespace()}:tabsync:${ServerSync.local.id}",
                        1L,
                        Serializers.gson.toJson(
                            PlayerTracker(localTrackedPlayers.toSet())
                        )
                    )
            }, 0L, 1L)

        thread {
            while (plugin.isEnabled)
            {
                kotlin.runCatching {
                    val updatedPlayers = ServerContainer.getServersInGroup("mip")
                        .filter { it.id != ServerSync.local.id }
                        .flatMap {
                            ScalaCommonsSpigot.instance
                                .kvConnection.sync()
                                .getAs<PlayerTracker>("${namespace()}:tabsync:${it.id}")
                                ?.trackedPlayers
                                ?: emptyList()
                        }
                        .filter { it.uuid !in localReceivingPlayers }
                        .associateBy { it.uuid }

                    val remote = remoteTrackedPlayers
                        .filter { it.key !in localReceivingPlayers }

                    val addedPlayers = updatedPlayers.filter { !remote.containsKey(it.key) }
                    val changedPlayers = remote
                        .filter {
                            val other = updatedPlayers[it.key]
                                ?: return@filter false

                            it.value.requiresUpdate(other)
                        }
                    val removedPlayers = remote.filter { !updatedPlayers.containsKey(it.key) }

                    addedPlayers.values.broadcastUpdates {
                        forEach {
                            remoteTrackedPlayers[it.uuid] = it
//                            println("Adding new players: ${it.uuid}")
                        }

                        TabSyncPacketCreator.create(this)
                    }

                    changedPlayers.values.broadcastUpdates {
                        forEach {
                            remoteTrackedPlayers[it.uuid] = it
//                            println("Updating existing player: ${it.uuid} -> ${it.latency} | ${it.displayName} | ${it.teamName}")

                            localReceivingPlayers.forEach context@{ other ->
                                val bukkitPlayer = Bukkit.getPlayer(other)
                                    ?: return@context

                                ScoreboardTeamPacketMod(
                                    it.teamName,
                                    arrayListOf(it.username),
                                    3
                                ).send(bukkitPlayer)
                            }
                        }

                        TabSyncPacketCreator.update(this)
                    }

                    removedPlayers.values.broadcastUpdates {
                        forEach {
                            remoteTrackedPlayers.remove(it.uuid)
//                            println("Removing old player: ${it.uuid}")
                        }

                        TabSyncPacketCreator.remove(this)
                    }
                }.onFailure {
                    it.printStackTrace()
                }

                Thread.sleep(100L)
            }
        }

        Events
            .subscribe(
                PlayerLoginEvent::class.java,
                EventPriority.MONITOR
            )
            .handler {
                updateLock.withLock {
                    localReceivingPlayers += it.player.uniqueId
                }
            }

        Events
            .subscribe(PlayerJoinEvent::class.java)
            .handler {
                Tasks.async {
                    sendBulkUpdatesTo(it.player)
                }
            }

        Events
            .subscribe(PlayerQuitEvent::class.java)
            .handler {
                Tasks.async {
                    updateLock.withLock {
                        localReceivingPlayers -= it.player.uniqueId
                    }
                }
            }
    }

    fun sendBulkUpdatesTo(player: Player)
    {
        val remote = remoteTrackedPlayers.values
            .filter { it.uuid !in localReceivingPlayers }

        val create = TabSyncPacketCreator.create(remote)
        for (wrapperPlayServerPlayerInfo in create)
        {
            Protocol.sendPacket(player, wrapperPlayServerPlayerInfo.handle)
        }

        remote.forEach {
            ScoreboardTeamPacketMod(
                it.teamName,
                arrayListOf(it.username),
                3
            ).send(player)
        }
    }

    private fun Collection<ImmutableTrackedPlayer>.broadcastUpdates(
        block: List<ImmutableTrackedPlayer>.() -> List<WrapperPlayServerPlayerInfo>
    )
    {
        if (isNotEmpty())
        {
            val addedPlayerPackets = block(toList())
            updateLock.withLock {
                for (localReceivingPlayer in localReceivingPlayers)
                {
                    val bukkitPlayer = Bukkit.getPlayer(localReceivingPlayer)
                        ?: continue

                    for (addedPlayerPacket in addedPlayerPackets)
                    {
                        Protocol.sendPacket(bukkitPlayer, addedPlayerPacket.handle)
                    }
                }
            }
        }
    }
}
