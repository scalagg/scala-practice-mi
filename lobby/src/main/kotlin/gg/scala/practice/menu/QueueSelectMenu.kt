package gg.scala.practice.menu

import com.cryptomorin.xseries.XMaterial
import gg.scala.practice.configuration.PracticeConfigurationService
import gg.scala.practice.kit.KitService
import gg.scala.practice.player.hotbar.LobbyHotbarService.buildBotSelectorMenu
import gg.scala.practice.profile.PracticeProfileService
import gg.scala.practice.queue.QueueType
import gg.scala.practice.services.GameManagerService
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player

/**
 * @author Limo
 * @date 6/26/2025
 */
class QueueSelectMenu : Menu("Select a Queue...")
{
    init
    {
        placeholder = true
    }

    override fun size(buttons: Map<Int, Button>) = 27

    override fun getButtons(player: Player) = mutableMapOf(
        11 to ItemBuilder
            .of(XMaterial.IRON_SWORD)
            .name("${CC.B_YELLOW}Unranked")
            .addToLore(
                "${CC.WHITE}Queue for a unranked",
                "${CC.WHITE}game with no",
                "${CC.WHITE}loss penalty.",
                "",
                "${CC.WHITE}Playing: ${CC.GOLD}${getPlaying(QueueType.Unranked, 1)}",
                "",
                "${CC.GREEN}Click to open!"
            )
            .toButton context@{ _, _ ->
                Button.playNeutral(player)
                JoinQueueMenu(player, QueueType.Unranked, 1).openMenu(player)
            },
        13 to ItemBuilder
            .of(XMaterial.DIAMOND_SWORD)
            .name("${CC.B_GOLD}Ranked")
            .addToLore(
                "${CC.WHITE}Queue for a ranked",
                "${CC.WHITE}game where wins and",
                "${CC.WHITE}losses affect your rank.",
                "",
                "${CC.WHITE}Playing: ${CC.GOLD}${getPlaying(QueueType.Ranked, 1)}",
                "",
                "${CC.GREEN}Click to open!"
            )
            .toButton context@{ _, _ ->
                val profile = PracticeProfileService.find(player)
                    ?: return@context

                val configuration = PracticeConfigurationService.cached()
                if (!player.isOp && !configuration.rankedQueueEnabled)
                {
                    player.sendMessage("${CC.RED}Ranked queues are temporarily disabled. Please try again later.")
                    return@context
                }
                if (
                    !player.hasPermission("practice.bypass-ranked-queue-requirements")
                    && profile.globalStatistics.totalWins < configuration.minimumWinRequirement()
                )
                {
                    player.sendMessage(
                        "${CC.RED}You must have at least ${
                            configuration.minimumWinRequirement()
                        } wins to queue for a Ranked kit! You currently have ${CC.BOLD}${
                            profile.globalStatistics.totalWins
                        }${CC.RED} wins."
                    )
                    return@context
                }

                if (profile.hasActiveRankedBan())
                {
                    profile.deliverRankedBanMessage(player)
                    return@context
                }
                Button.playNeutral(player)
                JoinQueueMenu(player, QueueType.Ranked, 1).openMenu(player)
            },
        15 to ItemBuilder
            .of(XMaterial.GOLDEN_SWORD)
            .name("${CC.B_GREEN}Bots")
            .addToLore(
                "${CC.WHITE}Fight against practice",
                "${CC.WHITE}bots of various",
                "${CC.WHITE}difficulties.",
                "",
                "${CC.WHITE}Playing: ${CC.GOLD}${getPlaying(QueueType.Robot, 1)}",
                "",
                "${CC.GREEN}Click to open!"
            )
            .toButton context@{ _, _ ->
                Button.playNeutral(player)
                val menu = buildBotSelectorMenu(player)
                    ?: return@context run {
                        player.sendMessage("${CC.RED}This feature is coming soon!")
                    }

                menu.openMenu(player)
            }
    )

    fun getPlaying(queueType: QueueType, teamSize: Int): Int {
        var total = 0

        KitService.cached().kits.values.forEach { kit ->
            val queueId = "${kit.id}:${queueType.name}:${teamSize}v$teamSize"
            val metadata = GameManagerService.buildQueueIdMetadataTracker(queueId)
            total += metadata.inGame
        }

        return total
    }
}