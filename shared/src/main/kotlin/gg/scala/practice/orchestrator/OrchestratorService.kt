package gg.scala.practice.orchestrator

import gg.scala.aware.AwareBuilder
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.util.Players
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.util.QuickAccess
import gg.scala.lemon.util.QuickAccess.username
import gg.scala.practice.suffixWhenDev
import me.lucko.helper.Schedulers
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.util.CC
import org.bukkit.Bukkit
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
@Service
object OrchestratorService
{
    private val listeningAware by lazy {
        AwareBuilder
            .of<AwareMessage>("practice:orchestrator-inhabitants".suffixWhenDev())
            .codec(AwareMessageCodec)
            .logger(Logger.getAnonymousLogger())
            .build()
    }

    fun createMessage(packet: String, vararg pairs: Pair<String, Any?>): AwareMessage =
        AwareMessage.of(packet, listeningAware, *pairs)

    @Configure
    fun configure()
    {
        var terminable: CompositeTerminable? = null
        listeningAware.listen("request-rollout") {
            val server = retrieve<String>("server")
            if (ServerSync.local.id != server)
            {
                return@listen
            }

            terminable?.closeAndReportException()
            terminable = CompositeTerminable.create()

            QuickAccess.sendGlobalBroadcast(
                "${CC.BLUE}[D] ${CC.D_AQUA}[${
                    ServerSync.getLocalGameServer().id
                }] ${CC.AQUA}Update is being rolled out on this instance. Instance will reboot once empty.",
                permission = "practice.admin"
            )

            Schedulers
                .sync()
                .runRepeating({ _ ->
                    if (Bukkit.getOnlinePlayers().isEmpty())
                    {
                        QuickAccess.sendGlobalBroadcast(
                            "${CC.BLUE}[D] ${CC.D_AQUA}[${
                                ServerSync.getLocalGameServer().id
                            }] ${CC.RED}Rollout requires an update. Server is rebooting!",
                            permission = "practice.admin"
                        )

                        terminable?.closeAndReportException()
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop")
                    }
                }, 0L, 20L)
                .bindWith(terminable!!)
        }

        listeningAware.listen("rollout-cancel") {
            val servers = retrieve<List<String>>("servers")
            if (ServerSync.local.id !in servers)
            {
                return@listen
            }

            terminable?.closeAndReportException()
            terminable = null

            QuickAccess.sendGlobalBroadcast(
                "${CC.BLUE}[D] ${CC.D_AQUA}[${
                    ServerSync.getLocalGameServer().id
                }] ${CC.AQUA}Update was cancelled on this instance. Instance will no longer reboot",
                permission = "practice.admin"
            )
        }
        listeningAware.connect().toCompletableFuture().join()

        createMessage(
            "unblock",
            "server" to ServerSync.local.id
        ).publish(
            AwareThreadContext.ASYNC,
            channel = "practice:orchestrator".suffixWhenDev()
        )
    }
}
