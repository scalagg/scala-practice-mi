package gg.scala.practice.games.loadout

import gg.scala.practice.kit.Kit
import gg.scala.practice.profile.loadout.Loadout
import gg.scala.practice.utilities.deepClone
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/25/2023
 */
class CustomLoadout(
    private val loadout: Loadout,
    private val kit: Kit
) : SelectedLoadout
{
    override fun displayName() = loadout.name
    override fun apply(player: Player)
    {
        kit.populate(player)
        player.inventory.contents = loadout.inventoryContents.deepClone()
        player.fixInventoryBasedOnRBTeamColor()
        player.updateInventory()
    }
}
