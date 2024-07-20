package gg.tropic.practice.games.loadout

import gg.tropic.practice.games.GameService
import gg.tropic.practice.kit.feature.FeatureFlag
import gg.tropic.practice.utilities.toRBTeamSide
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 7/19/2024
 */
fun Player.fixInventoryBasedOnRBTeamColor()
{
    val game = GameService.byPlayer(this)
    if (game != null)
    {
        val team = game.getTeamOf(player)
        if (game.flag(FeatureFlag.RedBlueTeams))
        {
            val teamSide = team.side.toRBTeamSide()
            player.inventory.armorContents.indices.forEach { index ->
                val itemStack = player.inventory.contents[index]
                    ?: return@forEach

                if (!itemStack.type.name.contains("LEATHER"))
                {
                    return@forEach
                }

                player.inventory.armorContents[index] = ItemBuilder
                    .copyOf(itemStack)
                    .color(teamSide.armorColor)
                    .build()
                player.updateInventory()
            }

            player.inventory.contents.indices.forEach { index ->
                val itemStack = player.inventory.contents[index]
                    ?: return@forEach

                if (itemStack.type == Material.WOOL || itemStack.type == Material.STAINED_CLAY)
                {
                    player.inventory.setItem(
                        index,
                        ItemBuilder
                            .copyOf(itemStack)
                            .data(teamSide.blockColor)
                            .build()
                    )
                    player.updateInventory()
                }
            }
        }
    }
}
