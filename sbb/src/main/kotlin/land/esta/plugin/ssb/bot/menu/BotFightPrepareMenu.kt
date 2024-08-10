package land.esta.plugin.ssb.bot.menu

import com.cryptomorin.xseries.XMaterial
import gg.scala.lemon.LemonConstants
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.profile.StaffProfileService
import gg.tropic.practice.Globals
import gg.tropic.practice.games.bots.BotGameMetadata
import gg.tropic.practice.games.bots.storeForUser
import gg.tropic.practice.kit.Kit
import gg.tropic.practice.kit.group.KitGroup
import gg.tropic.practice.kit.group.KitGroupService
import gg.tropic.practice.map.Map
import gg.tropic.practice.menu.TemplateMapMenu
import gg.tropic.practice.namespace
import gg.tropic.practice.player.LobbyPlayerService
import gg.tropic.practice.player.PlayerState
import gg.tropic.practice.queue.QueueService
import gg.tropic.practice.queue.QueueType
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 8/9/2024
 */
class BotFightPrepareMenu(
    private val botTypeNamespace: String,
    private val botTypeID: String? = null,
    private val kit: Kit? = null,
    private val map: Map? = null,
    private var numberOfBots: Int = 1
) : Menu("Preparing robot match...")
{
    init
    {
        placeholder = true
    }

    fun openMenuProtected(player: Player)
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

        openMenu(player)
    }

    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (manualClose)
        {
            Tasks.sync { BotFightMenu(player).openMenu(player) }
        }
    }

    override fun size(buttons: kotlin.collections.Map<Int, Button>) = 27
    override fun getButtons(player: Player) = mapOf(
        10 to ItemBuilder
            .copyOf(kit?.displayIcon ?: ItemStack(Material.DIAMOND_SWORD))
            .name("${CC.B_AQUA}Kit")
            .amount(1)
            .addToLore(
                "${CC.GRAY}Selected kit:",
                "${CC.WHITE}${kit?.displayName ?: "${CC.RED}None"}",
                "",
                "${CC.AQUA}Click to select!"
            )
            .toButton { _, _ ->
                Button.playNeutral(player)
                BotKitMenu { kit ->
                    BotFightPrepareMenu(
                        botTypeNamespace, botTypeID,
                        kit,
                        null /** new kit, new map. **/,
                        numberOfBots
                    ).openMenuProtected(player)
                }.openMenu(player)
            },
        11 to ItemBuilder
            .copyOf(map?.displayIcon ?: ItemStack(Material.MAP))
            .amount(1)
            .name("${CC.BD_AQUA}Map")
            .addToLore(
                "${CC.GRAY}Selected map:",
                "${CC.WHITE}${map?.displayName ?: "${CC.RED}None"}",
                "",
                "${CC.D_AQUA}Click to select!"
            )
            .toButton { _, _ ->
                if (!player.hasPermission("bot.custom-map-selection"))
                {
                    Button.playFail(player)
                    player.sendMessage("${CC.RED}You need ${CC.D_AQUA}VIP${CC.RED} rank to use this feature!")
                    player.sendMessage("${CC.RED}Purchase a rank on our webstore: ${CC.YELLOW}${LemonConstants.STORE_LINK}")
                    return@toButton
                }

                if (kit == null)
                {
                    Button.playFail(player)
                    player.sendMessage("${CC.RED}You must select a kit first!")
                    return@toButton
                }

                val groups = KitGroupService
                    .groupsOf(kit)
                    .map(KitGroup::id)
                    .toSet()

                Button.playNeutral(player)

                val selectionCallback = { map: Map? ->
                    BotFightPrepareMenu(
                        botTypeNamespace, botTypeID, kit, map, numberOfBots
                    ).openMenuProtected(player)
                }

                val menu = object : TemplateMapMenu()
                {
                    override fun filterDisplayOfMap(map: Map) = map.associatedKitGroups
                        .intersect(groups)
                        .isNotEmpty()

                    override fun itemDescriptionOf(player: Player, map: Map) = listOf(
                        "",
                        "${CC.GREEN}Click to select!"
                    )

                    override fun itemClicked(player: Player, map: Map, type: ClickType)
                    {
                        Button.playNeutral(player)
                        selectionCallback(map)
                    }

                    override fun getGlobalButtons(player: Player) = mutableMapOf(
                        4 to ItemBuilder
                            .of(Material.NETHER_STAR)
                            .name("${CC.B_AQUA}Random Map")
                            .addToLore(
                                "",
                                "${CC.AQUA}Click to select!"
                            )
                            .toButton { _, _ ->
                                selectionCallback(null)
                            }
                    )

                    override fun getPrePaginatedTitle(player: Player) = "Select a map..."

                    override fun onClose(player: Player, manualClose: Boolean)
                    {
                        if (manualClose)
                        {
                            Tasks.sync {
                                selectionCallback(null)
                            }
                        }
                    }
                }

                menu.openMenu(player)
            },
        12 to ItemBuilder
            .of(Material.ENDER_PORTAL_FRAME)
            .name("${CC.GREEN}Enemy Bots")
            .addToLore(
                "${CC.GRAY}Donators can add multiple bots",
                "${CC.GRAY}to their bot matches!",
                "",
                "${CC.D_AQUA}VIP allows 2 bots",
                "${CC.D_PURPLE}VIP${CC.WHITE}+${CC.D_PURPLE} allows 4 bots",
                "${CC.AQUA}MVP allows 6 bots",
                "${CC.GOLD}Solar allows 8 bots",
                "",
                "${CC.GRAY}Current: ${CC.WHITE}$numberOfBots",
                "",
                "${CC.GREEN}Left-Click to add!",
                "${CC.GREEN}Right-Click to remove!"
            )
            .toButton { _, type ->
                if (type!!.isRightClick)
                {
                    if (numberOfBots - 1 < 1)
                    {
                        Button.playFail(player)
                        player.sendMessage("${CC.RED}You must have at least 1 bot in your match!")
                        return@toButton
                    }

                    Button.playSuccess(player)
                    numberOfBots -= 1
                    openMenu(player)
                    return@toButton
                }

                if (!player.hasPermission("bot.amount.${numberOfBots + 1}"))
                {
                    Button.playFail(player)
                    player.sendMessage("${CC.RED}You cannot have ${numberOfBots + 1} bots in your match!")
                    return@toButton
                }

                if (numberOfBots + 1 > 10)
                {
                    Button.playFail(player)
                    player.sendMessage("${CC.RED}You may not have more than 10 bots in your match!")
                    return@toButton
                }

                Button.playSuccess(player)
                numberOfBots += 1
                openMenu(player)
            },
        16 to ItemBuilder
            .of(XMaterial.LIME_DYE)
            .name("${CC.GREEN}Play")
            .addToLore("${CC.GRAY}Start your bot match!")
            .apply {
                if (kit == null)
                {
                    addToLore(
                        "",
                        "${CC.RED}You must select a kit!"
                    )
                    return@apply
                }

                addToLore(
                    "",
                    "${CC.GREEN}Click to start!"
                )
            }
            .toButton { _, _ ->
                if (kit == null)
                {
                    return@toButton
                }

                player.closeInventory()
                CompletableFuture.runAsync {
                    val instances = mutableSetOf<UUID>()
                    repeat(numberOfBots) {
                        instances += Globals.POSSIBLE_PLAYER_BOT_UNIQUE_IDS[it]
                    }

                    BotGameMetadata(botTypeNamespace, botTypeID, kit.id, map?.name, botInstances = instances)
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
            }
    )
}
