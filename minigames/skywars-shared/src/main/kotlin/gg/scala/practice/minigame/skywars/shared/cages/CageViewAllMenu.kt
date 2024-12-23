package gg.scala.practice.minigame.skywars.shared.cages

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
class CageViewAllMenu : PaginatedMenu()
{
    override fun getGlobalButtons(player: Player) = mapOf(
        4 to ItemBuilder.of(XMaterial.LIME_DYE)
            .name("${CC.GREEN}Create")
            .addToLore("${CC.GRAY}Click to create!")
            .toButton { _, _ ->
                Button.playNeutral(player)
                player.closeInventory()

                InputPrompt()
                    .withText("${CC.GREEN}Enter an ID")
                    .acceptInput { _, id ->
                        if (CageDataSync.cached().cages[id] != null)
                        {
                            Button.playFail(player)
                            openMenu(player)
                            player.sendMessage("${CC.RED}Already exists!")
                            return@acceptInput
                        }

                        val sanitized = id.replace(" ", "_").lowercase()
                        SchemaCageSelectMenu(this) {
                            val cage = Cage(
                                sanitized,
                                sanitized,
                                ItemStack(Material.GLASS),
                                listOf("Default description!"),
                                it.id,
                                1000.0f,
                                CageRarity.Common
                            )

                            val cached = CageDataSync.cached()
                            cached.cages[sanitized] = cage
                            CageDataSync.sync(cached)
                            player.sendMessage("${CC.GREEN}Created!")
                        }.openMenu(player)
                    }
                    .start(player)
            }
    )

    override fun getAllPagesButtons(player: Player) = CageDataSync.cached().cages.values
        .map {
            ItemBuilder.copyOf(it.item)
                .name("${CC.GREEN}${it.displayName}")
                .setLore(
                    it.description
                )
                .addToLore(
                    "",
                    "${CC.GRAY}Rarity: ${it.rarity.fancy}",
                    "${CC.GRAY}Price: ${CC.GOLD}${it.price}",
                    "${CC.GRAY}Purchasable: ${if (it.purchasable) "${CC.GREEN}Yes" else "${CC.RED}No"}",
                    "${CC.GRAY}ID: ${CC.WHITE}${it.id}",
                    "",
                    "${CC.GRAY}Linked Schema: ${CC.WHITE}${it.linkedSchema}",
                    "",
                    "${CC.YELLOW}Click to edit!",
                    "${CC.RED}Click to delete!",
                )
                .toButton { _, type ->
                    if (type!!.isRightClick)
                    {
                        Button.playNeutral(player)

                        val cached = CageDataSync.cached()
                        cached.cages.remove(it.id)
                        CageDataSync.sync(cached)
                        
                        openMenu(player)
                        return@toButton
                    }

                    Button.playNeutral(player)
                    CageEditMenu(it).openMenu(player)
                }
        }
        .withIndex()
        .associate { it.index to it.value }

    override fun getPrePaginatedTitle(player: Player) = "Viewing all cages..."
}
