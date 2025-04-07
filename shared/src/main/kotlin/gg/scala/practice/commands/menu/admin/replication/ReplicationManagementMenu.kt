package gg.scala.practice.commands.menu.admin.replication

import com.cryptomorin.xseries.XMaterial
import gg.scala.practice.commands.menu.admin.replication.rollout.RolloutRegionSelection
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player

/**
 * Class created on 1/17/2025

 * @author Max C.
 * @project esta-practice
 * @website https://solo.to/redis
 */
class ReplicationManagementMenu: Menu()
{
    init
    {
        placeholder = true
    }

    override fun size(buttons: Map<Int, Button>): Int
    {
        return 9
    }

    override fun getTitle(player: Player): String
    {
        return "Select an Option"
    }

    override fun getButtons(player: Player): Map<Int, Button>
    {
        return mutableMapOf<Int, Button>().also {
            it[4] = ItemBuilder.of(XMaterial.ITEM_FRAME)
                .name("${CC.B_GREEN}Roll-Out Changes")
                .addToLore(
                    "${CC.GRAY}Roll out updates that spread across",
                    "${CC.GRAY}all replications",
                    " ",
                    "${CC.GREEN}Click to roll-out!"
                ).toButton { _, _ ->
                    RolloutRegionSelection().openMenu(player)
                }
        }
    }
}