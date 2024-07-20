package gg.tropic.practice.games.loadout

import gg.tropic.practice.games.GameService
import gg.tropic.practice.kit.feature.FeatureFlag
import gg.tropic.practice.utilities.toRBTeamSide
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
            sendMessage("Checking")
            player.inventory.armorContents.toList().forEachIndexed { index, itemStack ->
                if (itemStack == null)
                {
                    return@forEachIndexed
                }

                if (!itemStack.type.name.contains("LEATHER"))
                {
                    return@forEachIndexed
                }

                sendMessage("updating $index")
                player.inventory.armorContents[index] = net.evilblock.cubed.util.bukkit.ItemBuilder
                    .copyOf(itemStack)
                    .color(teamSide.armorColor)
                    .build()
            }

            player.inventory.contents.toList().forEachIndexed { index, itemStack ->
                if (itemStack == null)
                {
                    return@forEachIndexed
                }

                if (itemStack.type == org.bukkit.Material.WOOL || itemStack.type == org.bukkit.Material.STAINED_CLAY)
                {
                    sendMessage("updating $index inventory wool/stainedclay")
                    player.inventory.contents[index] = net.evilblock.cubed.util.bukkit.ItemBuilder
                        .copyOf(itemStack)
                        .data(teamSide.blockColor)
                        .build()
                }
            }
        }
    }
}
