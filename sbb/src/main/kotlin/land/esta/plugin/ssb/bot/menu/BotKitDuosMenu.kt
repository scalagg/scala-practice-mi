package land.esta.plugin.ssb.bot.menu

import gg.tropic.practice.Globals
import gg.tropic.practice.games.bots.BotGameMetadata
import gg.tropic.practice.games.bots.storeForUser
import gg.tropic.practice.kit.Kit
import gg.tropic.practice.menu.TemplateKitMenu
import gg.tropic.practice.queue.QueueService
import gg.tropic.practice.queue.QueueType
import gg.tropic.practice.queue.queueId
import gg.tropic.practice.services.GameManagerService
import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import java.util.concurrent.CompletableFuture

/**
 * Class created on 7/19/2024

 * @author Max C.
 * @project esta-practice
 * @website https://solo.to/redis
 */
class BotKitDuosMenu(private val difficultyProfile: SettingProfile) : TemplateKitMenu()
{
    init
    {
        autoUpdate = true
        updateAfterClick = true
    }

    override fun filterDisplayOfKit(player: Player, kit: Kit) = kit.queueSizes.any { it.second.contains(QueueType.Robot) }

    override fun itemTitleFor(player: Player, kit: Kit) = "${CC.GREEN}${kit.displayName}"
    override fun itemDescriptionOf(player: Player, kit: Kit): List<String>
    {
        val metadata = GameManagerService.buildQueueIdMetadataTracker(queueId {
            kit(kit)
            queueType(QueueType.Robot)
            teamSize(2)
        })

        return listOf(
            "",
            "${CC.YELLOW}Duos Queue:",
            "${CC.GRAY}Playing: ${CC.WHITE}${metadata.inGame}",
            "${CC.GRAY}Queuing: ${CC.WHITE}${metadata.inQueue}",
            "",
            "${CC.GREEN}Click to select!"
        )
    }

    override fun shouldIncludeKitDescription() = false

    private var locked = false
    override fun itemClicked(player: Player, kit: Kit, type: ClickType)
    {
        if (locked)
        {
            return
        }

        locked = true

        Button.playNeutral(player)
        player.closeInventory()

        CompletableFuture.runAsync {
            BotGameMetadata(
                difficultyProfile.id,
                null,
                kit.id,
                null,
                botInstances = setOf(
                    Globals.POSSIBLE_PLAYER_BOT_UNIQUE_IDS[0],
                    Globals.POSSIBLE_PLAYER_BOT_UNIQUE_IDS[1]
                )
            ).storeForUser(player.uniqueId)
        }.thenAccept {
            QueueService.joinQueue(kit, QueueType.Robot, 2, player)
            Button.playNeutral(player)

            player.sendMessage(
                "${CC.GREEN}Joined the queue for ${CC.PRI}Duos ${QueueType.Robot.name} ${kit.displayName}${CC.GREEN}!"
            )
        }.exceptionally {
            it.printStackTrace()
            player.sendMessage("${CC.RED}We weren't able to create a bot fight for you. Please report this to an administrator!")
            return@exceptionally null
        }
    }

    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (manualClose)
        {
            Tasks.sync {
                BotFightMenu(player).openMenu(player)
            }
        }
    }

    override fun getPrePaginatedTitle(player: Player) = "Queuing Robot Duos..."
}
