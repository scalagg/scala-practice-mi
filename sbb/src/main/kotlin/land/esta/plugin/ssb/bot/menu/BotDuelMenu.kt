package land.esta.plugin.ssb.bot.menu

import gg.scala.basics.plugin.settings.SettingMenu
import land.esta.plugin.ssb.bots.setting.SettingManager
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * Class created on 7/19/2024

 * @author Max C.
 * @project esta-practice
 * @website https://solo.to/redis
 */
class BotDuelMenu : Menu("Select a Difficulty...")
{
    init
    {
        placeholder = true
    }

    override fun size(buttons: Map<Int, Button>): Int = 27
    override fun getButtons(player: Player): Map<Int, Button> = mapOf(
        10 to ItemBuilder.of(Material.WOOL)
            .data(5)
            .name("&aEasy")
            .setLore(SettingManager.defaultManager.getMenuDescription("EASY"))
            .toButton(),
        11 to ItemBuilder.of(Material.WOOL)
            .data(4)
            .name("&eNormal")
            .setLore(SettingManager.defaultManager.getMenuDescription("NORMAL"))
            .toButton(),
        12 to ItemBuilder.of(Material.WOOL)
            .data(14)
            .name("&cHard")
            .setLore(SettingManager.defaultManager.getMenuDescription("HARD"))
            .toButton(),
        13 to ItemBuilder.of(Material.WOOL)
            .data(14)
            .name("&4Expert")
            .setLore(SettingManager.defaultManager.getMenuDescription("EXPERT"))
            .toButton(),
        14 to ItemBuilder.of(Material.WOOL)
            .data(10)
            .name("&5Nightmare")
            .setLore(SettingManager.defaultManager.getMenuDescription("NIGHTMARE"))
            .toButton()
    )
}