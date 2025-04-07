package gg.scala.practice.commands.menu.admin.replication.rollout

import com.cryptomorin.xseries.XMaterial
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.practice.orchestrator.OrchestratorService
import gg.scala.practice.region.Region
import gg.scala.practice.suffixWhenDev
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player

/**
 * Class created on 1/18/2025

 * @author Max C.
 * @project esta-practice
 * @website https://solo.to/redis
 */
class RolloutDevSelectionMenu(val region: Region): Menu()
{
    init
    {
        placeholder = true
    }

    override fun getTitle(player: Player): String
    {
        return "Select an Option"
    }

    override fun size(buttons: Map<Int, Button>): Int
    {
        return 9
    }

    override fun getButtons(player: Player): Map<Int, Button>
    {
        return mutableMapOf<Int, Button>().also {
            it[3] = ItemBuilder.of(XMaterial.COMPASS)
                .name("${CC.B_GREEN}Use Developer Mode")
                .addToLore(
                    "${CC.GRAY}Select whether or not you want this",
                    "${CC.GRAY}rollout to be ran in developer mode.",
                    "",
                    "${CC.GREEN}Click to select!"
                ).toButton { _, _ ->
                    player.closeInventory()
                    player.sendMessage("${CC.GREEN}We've requested an update rollout, please wait a moment. You should see incremental progress indicated by ServerSync messages.")

                    OrchestratorService
                        .createMessage(
                            "start",
                            "region" to region,
                            "dev" to true,
                            "player" to player.uniqueId
                        )
                        .publish(
                            AwareThreadContext.ASYNC,
                            channel = "practice:orchestrator".suffixWhenDev()
                        )
                }

            it[5] = ItemBuilder.of(XMaterial.CLOCK)
                .name("${CC.B_RED}Don't Use Developer Mode")
                .addToLore(
                    "${CC.GRAY}Select whether or not you want this",
                    "${CC.GRAY}rollout to be ran in normal mode",
                    "",
                    "${CC.GREEN}Click to select!"
                ).toButton { _, _ ->
                    player.closeInventory()
                    player.sendMessage("${CC.GREEN}We've requested an update rollout, please wait a moment. You should see incremental progress indicated by ServerSync messages.")

                    OrchestratorService
                        .createMessage(
                            "start",
                            "region" to region,
                            "dev" to false,
                            "player" to player.uniqueId
                        )
                        .publish(
                            AwareThreadContext.ASYNC,
                            channel = "practice:orchestrator".suffixWhenDev()
                        )
                }
        }
    }
}