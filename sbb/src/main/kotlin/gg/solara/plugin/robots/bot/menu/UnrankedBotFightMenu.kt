package gg.solara.plugin.robots.bot.menu

import gg.solara.plugin.robots.bot.profile.UnrankedBotsService
import gg.tropic.practice.Globals
import gg.tropic.practice.games.bots.BotGameMetadata
import gg.tropic.practice.games.bots.storeForUser
import gg.tropic.practice.kit.KitService
import gg.tropic.practice.queue.QueueService
import gg.tropic.practice.queue.QueueType
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author TastyCake
 * @date 9/7/2024
 */
class UnrankedBotFightMenu(
    player: Player
)
{
    init
    {
        BotKitMenu(player) { kit ->
            if (kit == null) {
                player.closeInventory()
                player.sendMessage("${CC.RED}You must select a kit!")
                return@BotKitMenu
            }
            player.closeInventory()
            CompletableFuture.runAsync {
                val instances = mutableSetOf<UUID>()
                repeat(1) {
                    instances += Globals.POSSIBLE_PLAYER_BOT_UNIQUE_IDS[it]
                }

                BotGameMetadata("UNRANKED", "", kit.id, null, botInstances = instances)
                    .storeForUser(player.uniqueId)
            }.thenAccept {
                QueueService.joinQueue(kit, QueueType.Robot, 1, player)
                Button.playNeutral(player)

                player.sendMessage(
                    "${CC.GREEN}Preparing your ${CC.PRI}${QueueType.Robot.name} ${kit.displayName}${CC.GREEN} match..."
                )
            }.exceptionally {
                it.printStackTrace()
                player.sendMessage("${CC.RED}We weren't able to create a bot fight for you. Please report this to an administrator!")
                return@exceptionally null
            }
        }.openMenu(player)
    }
}