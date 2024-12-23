package gg.scala.practice.menu

import com.cryptomorin.xseries.XMaterial
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.profile.StaffProfileService
import gg.scala.practice.kit.Kit
import gg.scala.practice.kit.category.KitCategoryService
import gg.scala.practice.kit.feature.FeatureFlag
import gg.scala.practice.leaderboards.Reference
import gg.scala.practice.leaderboards.ReferenceLeaderboardType
import gg.scala.practice.player.LobbyPlayerService
import gg.scala.practice.player.PlayerState
import gg.scala.practice.queue.QueueService
import gg.scala.practice.queue.QueueType
import gg.scala.practice.services.GameManagerService
import gg.scala.practice.services.LeaderboardManagerService
import gg.scala.practice.utilities.MenuConstants
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.math.Numbers
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemFlag

/**
 * @author GrowlyX
 * @since 9/24/2023
 */
class JoinQueueMenu(
    player: Player,
    private val queueType: QueueType,
    private val teamSize: Int
) : TemplateKitMenu(player, dynamic = true)
{
    init
    {
        async = true
        autoUpdate = true
    }
/*
    override fun getItemAmount(player: Player, kit: Kit): Int
    {
        val metadata = GameManagerService.buildQueueIdMetadataTracker(queueId {
            kit(kit)
            queueType(queueType)
            teamSize(teamSize)
        })

        return metadata.inGame.coerceIn(1..64)
    }*/

    override fun getAutoUpdateTicks() = 500L
    override fun filterDisplayOfKit(player: Player, kit: Kit) = kit.queueSizes
        .any {
            it.first == teamSize && queueType in it.second
        }

    override fun size(buttons: Map<Int, Button>) = 54

    override fun getMaxItemsPerPage(player: Player) = MenuConstants.SLOTS_TRIPLE.size

    override fun getAllPagesButtonSlots() = MenuConstants.SLOTS_TRIPLE.map { it + 9 }

    override fun getGlobalButtons(player: Player): Map<Int, Button>
    {

        val buttons = mutableMapOf<Int, Button>()

        for (i in 9..17)
        {
            buttons[i] = PLACEHOLDER
        }

        var i = 1
        i++

        if (category == null)
        {
            buttons[i + 9] = ItemBuilder
                .of(XMaterial.LIME_STAINED_GLASS_PANE)
                .name(" ")
                .toButton()
        }

        buttons[i] = ItemBuilder
            .of(Material.EMERALD)
            .name("${CC.YELLOW}All Kits")
            .addToLore("${CC.GRAY}View all of our kits!")
            .addToLore(
                "",
                "${CC.GREEN}Click to view!"
            )
            .toButton { _, _ ->
                if (category == null)
                {
                    return@toButton
                }

                category = null
                openMenu(player)

                player.playSound(
                    player.location, Sound.NOTE_PLING, 1.0f, 1.2f
                )
            }

        KitCategoryService.cached()
            .categories
            .forEach {
                i++

                if (category == it)
                {
                    buttons[i + 9] = ItemBuilder
                        .of(XMaterial.LIME_STAINED_GLASS_PANE)
                        .name(" ")
                        .toButton()
                }

                buttons[i] = ItemBuilder
                    .of(it.item)
                    .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .data(if (it.item == Material.POTION) 16421 else 0)
                    .name("${CC.YELLOW}${it.name}${
                        if (it.newCategory) " ${CC.B_AQUA}NEW!" else ""
                    }")
                    .addFlags(
                        ItemFlag.HIDE_ATTRIBUTES,
                        ItemFlag.HIDE_ENCHANTS,
                        ItemFlag.HIDE_POTION_EFFECTS
                    )
                    .setLore(it.description.map { "${CC.GRAY}$it" })
                    .addToLore(
                        "",
                        "${CC.GREEN}Click to view!"
                    )
                    .toButton { _, _ ->
                        if (category == it)
                        {
                            return@toButton
                        }

                        category = it
                        openMenu(player)

                        player.playSound(
                            player.location, Sound.NOTE_PLING, 1.0f, 1.2f
                        )
                    }
            }

        return buttons
    }


    override fun itemTitleFor(player: Player, kit: Kit) = "${CC.PRI}${kit.displayName}${
        if (kit.features(FeatureFlag.NewlyCreated)) " ${CC.B_AQUA}NEW!" else ""
    }"

    override fun itemDescriptionOf(player: Player, kit: Kit): List<String>
    {
        val queueId = "${kit.id}:${queueType.name}:${teamSize}v${teamSize}"
        val metadata = GameManagerService.buildQueueIdMetadataTracker(queueId)

        return listOf(
            "${CC.WHITE}Playing: ${CC.PRI}${metadata.inGame}",
            "${CC.WHITE}Queuing: ${CC.PRI}${metadata.inQueue}",
            "",
            *local3ScoreDescriptionOf(
                player,
                Reference(if (queueType == QueueType.Ranked) ReferenceLeaderboardType.ELO else ReferenceLeaderboardType.UnrankedWinStreak, kit.id),
                if (queueType == QueueType.Ranked) "ELO" else "Daily Streak"
            ).toTypedArray(),
            "",
            "${CC.GREEN}Click to queue!"
        )
    }

    override fun asyncLoadResources(player: Player, callback: (Boolean) -> Unit)
    {
        callback(true)
    }

    private val scoreCache = mutableMapOf<Reference, Pair<Long?, Long?>>()
    private fun local3ScoreDescriptionOf(player: Player, reference: Reference, label: String): List<String>
    {
        var cachedScore = scoreCache[reference]

        if (cachedScore == null)
        {
            scoreCache[reference] = LeaderboardManagerService
                .getUserRankWithScore(player.uniqueId, reference)
                .join()
            cachedScore = scoreCache[reference]!!
        }

        val personalScore = listOf(
            "${CC.PRI}Your $label: ${CC.WHITE}${cachedScore.second?.run { Numbers.format(this) } ?: "${CC.D_GRAY}..."} ${
                CC.GRAY + (cachedScore.first?.run { "(#${Numbers.format(this + 1)})" } ?: "")
            }"
        )

        return personalScore + LeaderboardManagerService
            .getCachedFormattedLeaderboards(reference)
            .take(3)
    }

    override fun itemClicked(player: Player, kit: Kit, type: ClickType)
    {
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
        QueueService.joinQueue(kit, queueType, teamSize, player)

        Button.playNeutral(player)
        player.sendMessage(
            "${CC.GREEN}You have joined the ${CC.PRI}${queueType.name} ${teamSize}v$teamSize ${kit.displayName}${CC.GREEN} queue!"
        )
    }

    override fun getPrePaginatedTitle(player: Player) =
        "Queueing ${queueType.name} ${teamSize}v$teamSize..."
}
