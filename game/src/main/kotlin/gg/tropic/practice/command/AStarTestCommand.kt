package gg.tropic.practice.command

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.flavor.inject.Inject
import gg.tropic.practice.PracticeGame
import me.lucko.helper.Schedulers
import net.evilblock.cubed.entity.npc.pathfinding.AStar
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import org.bukkit.Effect

/**
 * @author GrowlyX
 * @since 12/24/2022
 */
@AutoRegister
object AStarTestCommand : ScalaCommand()
{
    @Inject
    lateinit var plugin: PracticeGame

    @CommandAlias("astar-test")
    @CommandPermission("op")
    fun onAStarTest(player: ScalaPlayer)
    {
        InputPrompt()
            .withText("Type anything once you're at the start location")
            .acceptInput { _, _ ->
                val startLocation = player.bukkit().location
                player.sendMessage("Marked first location")

                Schedulers
                    .sync()
                    .runLater({
                        InputPrompt()
                            .withText("Type anything once you're at the end location")
                            .acceptInput { _, _ ->
                                val endLocation = player.bukkit().location
                                val aStar = AStar(
                                    startLocation, endLocation,
                                    maxFallDistance = 20.0,
                                    canClimbLadders = true
                                )

                                val pathCalc = System.currentTimeMillis()
                                val path = aStar.getPath().toMutableList()
                                val pathCalcEnd = System.currentTimeMillis()

                                player.sendMessage(
                                    "${CC.GRAY}Took ${pathCalcEnd - pathCalc}ms to calculate path using AStar"
                                )

                                var nodes = 0

                                Schedulers
                                    .sync()
                                    .runRepeating({ task ->
                                        player.bukkit().world.playEffect(
                                            endLocation, Effect.FLAME, 0, 3
                                        )

                                        if (!player.bukkit().isOnline)
                                        {
                                            task.closeAndReportException()
                                            return@runRepeating
                                        }

                                        if (path.isEmpty())
                                        {
                                            task.closeAndReportException()
                                            player.sendMessage("${CC.GREEN}Path complete")
                                            return@runRepeating
                                        }

                                        nodes += 1

                                        val first = path.removeFirst()
                                        player.teleport(first)
                                        player.sendMessage(
                                            "${CC.YELLOW}Teleported to node $nodes. ${path.size} nodes remaining"
                                        )
                                    }, 0L, 3L)
                            }
                            .start(player.bukkit())
                    }, 5L)
            }
            .start(player.bukkit())
    }
}
