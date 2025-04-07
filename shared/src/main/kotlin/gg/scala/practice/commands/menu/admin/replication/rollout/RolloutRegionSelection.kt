package gg.scala.practice.commands.menu.admin.replication.rollout

import com.cryptomorin.xseries.XMaterial
import gg.scala.practice.region.Region
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
class RolloutRegionSelection: Menu()
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
        return "Select Rollout Region"
    }

    override fun getButtons(player: Player): Map<Int, Button>
    {
        return mutableMapOf<Int, Button>().also {
            it[3] = ItemBuilder.of(XMaterial.RED_WOOL)
                .name("${CC.B_RED}North America")
                .addToLore(
                    "${CC.GRAY}Roll-out changes on all replications",
                    "${CC.GRAY}that match the region ${CC.RED}North America",
                    "",
                    "${CC.GREEN}Click to select!"
                ).toButton { _, _ ->
                    RolloutDevSelectionMenu(Region.NA).openMenu(player)
                }

            it[5] = ItemBuilder.of(XMaterial.BLUE_WOOL)
                .name("${CC.B_BLUE}Europe")
                .addToLore(
                    "${CC.GRAY}Roll-out changes on all replications",
                    "${CC.GRAY}that match the region ${CC.BLUE}Europe",
                    "",
                    "${CC.GREEN}Click to select!"
                ).toButton { _, _ ->
                    RolloutDevSelectionMenu(Region.EU).openMenu(player)
                }
        }
    }
}