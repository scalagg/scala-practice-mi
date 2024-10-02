package gg.solara.practice.minigame.hctranked.menu

import gg.solara.practice.minigame.hctranked.shared.profile.HCTRankedProfileService
import gg.solara.practice.minigame.hctranked.shared.pvpclass.PvPClassType
import gg.tropic.practice.profile.PracticeProfileService
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author Topu
 * @date 10/2/2024
 */
class PvPClassSelectionMenu : Menu("Select a pvp class!")
{
    init
    {
        placeholder = true
    }

    override fun size(buttons: Map<Int, Button>) = 27
    override fun getButtons(player: Player) = mapOf(
        11 to ItemBuilder
            .of(Material.DIAMOND_CHESTPLATE)
            .name("${CC.AQUA}Diamond Class")
            .addToLore(
                "${CC.GREEN}Click to select!"
            )
            .toButton { _, _ ->
                HCTRankedProfileService.find(player)?.selectedClass = PvPClassType.DIAMOND
                player.sendMessage("${CC.GREEN}You have equipped the ${CC.AQUA}Diamond ${CC.GREEN}class.")
            },
        13 to ItemBuilder
            .of(Material.GOLD_CHESTPLATE)
            .name("${CC.YELLOW}Bard Class")
            .addToLore(
                "${CC.GREEN}Click to select!"
            )
            .toButton { _, _ ->
                HCTRankedProfileService.find(player)?.selectedClass = PvPClassType.BARD
                player.sendMessage("${CC.GREEN}You have equipped the ${CC.YELLOW}Bard ${CC.GREEN}class.")
            },
        15 to ItemBuilder
            .of(Material.LEATHER_CHESTPLATE)
            .name("${CC.D_PURPLE}Archer Class")
            .addToLore(
                "${CC.GREEN}Click to select!"
            )
            .toButton { _, _ ->
                HCTRankedProfileService.find(player)?.selectedClass = PvPClassType.ARCHER
                player.sendMessage("${CC.GREEN}You have equipped the ${CC.AQUA}Archer ${CC.GREEN}class.")
            },
    )
}