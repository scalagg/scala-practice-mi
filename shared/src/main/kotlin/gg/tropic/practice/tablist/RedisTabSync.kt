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
import org.bukkit.event.player.PlayerJoinEvent
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
                        .associateBy { it.uuid }

                    val addedPlayers = updatedPlayers.filter { !remoteTrackedPlayers.containsKey(it.key) }
                    val changedPlayers = remoteTrackedPlayers
                        .filter {
                            val other = updatedPlayers[it.key]
                                ?: return@filter false

                            it.value.requiresUpdate(other)
                        }
                    val removedPlayers = remoteTrackedPlayers.filter { !updatedPlayers.containsKey(it.key) }

                    addedPlayers.values.broadcastUpdates {
                        println("adding players")
                        TabSyncPacketCreator.create(this)
                    }

                    updateLock.withLock {
                        changedPlayers.values.forEach {
                            remoteTrackedPlayers[it.uuid] = it
                            localReceivingPlayers.forEach context@{ other ->
                                val bukkitPlayer = Bukkit.getPlayer(other)
                                    ?: return@context

                                ScoreboardTeamPacketMod(
                                    it.teamName,
                                    arrayListOf(it.username),
                                    3
                                ).send(bukkitPlayer)
                                println("Sent update mod")
                            }
                        }
                    }

                    changedPlayers.values.broadcastUpdates {
                        println("updating players")
                        TabSyncPacketCreator.update(this)
                    }

                    updateLock.withLock {
                        updatedPlayers.forEach { (t, u) ->
                            remoteTrackedPlayers[t] = u
                        }
                    }

                    removedPlayers.values.broadcastUpdates {
                        println("removing players")
                        TabSyncPacketCreator.remove(this)
                    }

                    updateLock.withLock {
                        removedPlayers.keys.forEach {
                            remoteTrackedPlayers.remove(it)
                        }
                    }
                }.onFailure {
                    it.printStackTrace()
                }

                Thread.sleep(100L)
            }
        }

        Events
            .subscribe(PlayerJoinEvent::class.java)
            .handler {
                Tasks.async {
                    updateLock.withLock {
                        sendBulkUpdatesTo(it.player)
                        localReceivingPlayers += it.player.uniqueId
                    }
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
        val create = TabSyncPacketCreator.create(remoteTrackedPlayers.values.toList())
        for (wrapperPlayServerPlayerInfo in create)
        {
            Protocol.sendPacket(player, wrapperPlayServerPlayerInfo.handle)
        }

        remoteTrackedPlayers.values.forEach {
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
