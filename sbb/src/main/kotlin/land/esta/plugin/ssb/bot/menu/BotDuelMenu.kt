package land.esta.plugin.ssb.bot.menu

import land.esta.plugin.ssb.SmartBotsBackend
import land.esta.plugin.ssb.bots.setting.SettingManager
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

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
            .setLore(SettingManager.defaultManager.getMenuDescription("EASY", "&aEasy"))
            .toButton { _, _ ->
                BotKitMenu("EASY").openMenu(player)
            },
        11 to ItemBuilder.of(Material.WOOL)
            .data(4)
            .name("&eNormal")
            .setLore(SettingManager.defaultManager.getMenuDescription("NORMAL", "&eNormal"))
            .toButton { _, _ ->
                BotKitMenu("NORMAL").openMenu(player)
            },
        12 to ItemBuilder.of(Material.WOOL)
            .data(14)
            .name("&cHard")
            .setLore(SettingManager.defaultManager.getMenuDescription("HARD", "&cHard"))
            .toButton { _, _ ->
                BotKitMenu("HARD").openMenu(player)
            },
        13 to ItemBuilder.of(Material.WOOL)
            .data(14)
            .name("&4Expert")
            .setLore(SettingManager.defaultManager.getMenuDescription("EXPERT", "&cExpert"))
            .toButton { _, _ ->
                BotKitMenu("EXPERT").openMenu(player)
            },
        14 to ItemBuilder.of(Material.WOOL)
            .data(10)
            .name("&5Nightmare")
            .setLore(SettingManager.defaultManager.getMenuDescription("NIGHTMARE", "&5Nightmare"))
            .toButton { _, _ ->
                BotKitMenu("NIGHTMARE").openMenu(player)
            },
        16 to ItemBuilder.of(Material.NETHER_STAR)
            .name("&6Custom")
            .addToLore("${CC.GRAY}Available for ${CC.D_AQUA}VIP${CC.GRAY} and up!")
            .setLore(SettingManager.defaultManager.getMenuDescription(player.uniqueId.toString(), "&6Custom"))
            .addToLore("${CC.YELLOW}Right-Click to customize!")
            .toButton { _, type ->
                if (!player.hasPermission("botgui.use"))
                {
                    player.sendMessage("${CC.RED}You cannot use this feature!")
                    return@toButton
                }

                if (type == ClickType.RIGHT)
                {
                    val data = SmartBotsBackend.instance
                        .dataManager
                        .getPlayer(player)

                    val gui = data.gui
                    if (gui.loaded)
                    {
                        gui.mainGui.openGui()
                    }
                    return@toButton
                }

                BotKitMenu(player.uniqueId.toString()).openMenu(player)
            },
    )
}
