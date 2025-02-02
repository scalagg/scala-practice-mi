package gg.scala.practice.menu.party

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.lemon.handler.PlayerHandler.find
import gg.scala.parties.model.Party
import gg.scala.parties.service.PartyService.loadedParties
import gg.scala.practice.utilities.MenuConstants
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.function.Consumer

/**
 * @author Topu
 * @date 2/2/2025
 */
class OtherPartiesMenu : PaginatedMenu()
{
    init
    {
        placeholdBorders = true
        autoUpdate = true
    }

    override fun size(buttons: Map<Int, Button>): Int
    {
        return 36
    }

    override fun getPrePaginatedTitle(player: Player): String
    {
        return "Other Parties"
    }

    override fun getAllPagesButtonSlots(): List<Int>
    {
        return MenuConstants.SLOTS_DOUBLE
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        val buttonMap: MutableMap<Int, Button> = HashMap()

        loadedParties.values.stream()
            .filter { party: Party -> Bukkit.getPlayer(party.leader.uniqueId) != null }
            .filter { party: Party -> !party.includedMembersOnline().contains(player.uniqueId) }
            .forEach { party: Party? -> buttonMap[buttonMap.size] = PartyButton(party) }

        return buttonMap
    }

    override fun onClose(player: Player, manualClose: Boolean)
    {
    }

    private class PartyButton(private val party: Party?) : Button()
    {

        override fun getButtonItem(player: Player): ItemStack
        {
            val leader = Bukkit.getPlayer(
                party!!.leader.uniqueId
            )

            val stringList: MutableList<String> = ArrayList()
            stringList.add(CC.GRAY + "Participants:")

            party.includedMembersOnline().forEach(Consumer { uuid: UUID? ->
                val member = Bukkit.getPlayer(uuid)
                if (member != null)
                {
                    stringList.add(CC.WHITE + " - " + member.name)
                }
            })

            stringList.add("")
            stringList.add(CC.GREEN + "Click to send duel!")

            return ItemBuilder(Material.SKULL_ITEM)
                .data(3.toShort())
                .owner(leader.name)
                .name(CC.GREEN + leader.name + "'s Party")
                .setLore(stringList)
                .build()
        }

        override fun clicked(
            player: Player, slot: Int,
            clickType: ClickType,
            view: InventoryView
        )
        {
            val leader = Bukkit
                .getPlayer(party!!.leader.uniqueId)

            if (leader != null)
            {
                player.closeInventory()

                try
                {
                    player.performCommand("duel ${leader.name}")
                } catch (exception: ConditionFailedException)
                {
                    player.sendMessage(CC.RED + exception.message)
                }
            } else
            {
                player.sendMessage(CC.RED + "The leader of this party is no longer online.")
            }
        }
    }
}