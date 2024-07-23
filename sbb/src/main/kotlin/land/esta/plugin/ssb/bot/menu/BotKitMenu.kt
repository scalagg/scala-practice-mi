package land.esta.plugin.ssb.bot.menu

import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.profile.StaffProfileService
import gg.tropic.practice.kit.Kit
import gg.tropic.practice.menu.template.TemplateKitMenu
import gg.tropic.practice.namespace
import gg.tropic.practice.player.LobbyPlayerService
import gg.tropic.practice.player.PlayerState
import gg.tropic.practice.queue.QueueService
import gg.tropic.practice.queue.QueueType
import gg.tropic.practice.region.Region
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import java.util.concurrent.CompletableFuture

/**
 * Class created on 7/19/2024

 * @author Max C.
 * @project esta-practice
 * @website https://solo.to/redis
 */
class BotKitMenu(private val identifier: String, private val selection: String? = null) : TemplateKitMenu()
{
    private var kitSelectionLock = false
    private var regionSelection: Region? = null

    init
    {
        updateAfterClick = true
    }

    override fun getGlobalButtons(player: Player) = mapOf(
        4 to ItemBuilder
            .copyOf(
                object : TexturedHeadButton(Constants.GLOBE_ICON){}.getButtonItem(player)
            )
            .name("${CC.GREEN}Region")
            .addToLore(
                "${
                    if (regionSelection == null) CC.B_WHITE else CC.GRAY
                }Closest ${CC.D_GRAY}${
                    Constants.THIN_VERTICAL_LINE
                } ${
                    if (regionSelection == Region.NA) CC.B_WHITE else CC.GRAY
                }NA ${CC.D_GRAY}${
                    Constants.THIN_VERTICAL_LINE
                } ${
                    if (regionSelection == Region.EU) CC.B_WHITE else CC.GRAY
                }EU"
            )
            .toButton { _, _ ->
                val oldRegionSelection = regionSelection
                regionSelection = when (oldRegionSelection)
                {
                    null -> Region.NA
                    Region.NA -> Region.EU
                    Region.EU -> null
                    Region.Both -> null
                }

                Button.playNeutral(player)
            }
    )

    override fun filterDisplayOfKit(player: Player, kit: Kit) = kit.queueSizes.any { it.second.contains(QueueType.Robot) }

    override fun itemTitleFor(player: Player, kit: Kit) = "${CC.B_GREEN}${kit.displayName}"
    override fun itemDescriptionOf(player: Player, kit: Kit) = listOf(
        "",
        "${CC.GREEN}Click to select!"
    )

    override fun shouldIncludeKitDescription() = false

    override fun itemClicked(player: Player, kit: Kit, type: ClickType)
    {
        if (kitSelectionLock)
        {
            return
        }

        val lobbyPlayer = LobbyPlayerService
            .find(player.uniqueId)
            ?: return

        if (lobbyPlayer.state != PlayerState.Idle)
        {
            player.sendMessage("${CC.RED}You cannot join a queue right now!")
            return
        }

        if (player.hasMetadata("vanished"))
        {
            player.sendMessage("${CC.RED}You are currently in vanish! Use ${CC.B}/vanish${CC.RED} to be able to join a queue.")
            return
        }

        val profile = StaffProfileService.find(player)
        if (profile != null && player.hasPermission(ScalaStaffPlugin.STAFF_NODE))
        {
            if (profile.staffAutoVanishEnabled)
            {
                player.sendMessage("${CC.RED}You currently have AutoVanish enabled! Use ${CC.B}/toggleautovanish${CC.RED} to be able to join a queue.")
                return
            }
        }

        player.closeInventory()

        kitSelectionLock = true

        CompletableFuture.runAsync {
            ScalaCommonsSpigot.instance.kvConnection.sync()
                .set(
                    "${namespace()}:bot-selection:${player.uniqueId}",
                    if (selection == null) identifier else "$identifier:$selection"
                )
        }.thenAccept {
            QueueService.joinQueue(kit, QueueType.Robot, 1, player)
        }

        Button.playNeutral(player)
        player.sendMessage(
            "${CC.GREEN}You have joined the ${CC.PRI}${QueueType.Robot.name} ${kit.displayName}${CC.GREEN} queue!"
        )
    }

    override fun getPrePaginatedTitle(player: Player) = "Select a kit..."
}
