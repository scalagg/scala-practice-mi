package gg.solara.practice.minigame.skywars.shared.cages

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.menus.SelectItemStackMenu
import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Color
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
class CageEditMenu(private val cage: Cage) : Menu("Editing cage ${cage.displayName}${CC.D_GRAY}...")
{
    init
    {
        placeholder = true
    }

    override fun size(buttons: Map<Int, Button>) = 27
    override fun getButtons(player: Player) = mapOf(
        10 to ItemBuilder
            .of(Material.NAME_TAG)
            .name("${CC.YELLOW}Name: ${CC.WHITE}${cage.displayName}")
            .addToLore("${CC.GRAY}Click to edit...")
            .toButton { _, _ ->
                Button.playNeutral(player)
                player.closeInventory()

                InputPrompt()
                    .withText("${CC.GREEN}Enter a new display name!")
                    .acceptInput { _, newDisplayName ->
                        cage.editAndSave { displayName = Color.translate(newDisplayName) }

                        player.sendMessage("${CC.GREEN}Saved display name!")
                        openMenu(player)
                    }
                    .start(player)
            },
        11 to ItemBuilder
            .of(Material.EMERALD)
            .name("${CC.YELLOW}Price: ${CC.GOLD}${cage.price} coins")
            .addToLore("${CC.GRAY}Click to edit...")
            .toButton { _, _ ->
                Button.playNeutral(player)
                player.closeInventory()

                NumberPrompt()
                    .withText("${CC.GREEN}Enter a new price!")
                    .acceptInput { newPrice ->
                        cage.editAndSave { price = newPrice.toFloat() }

                        player.sendMessage("${CC.GREEN}Saved price!")
                        openMenu(player)
                    }
                    .start(player)
            },
        12 to ItemBuilder
            .of(Material.EMERALD)
            .name("${CC.YELLOW}Purchasable: ${CC.GOLD}${if (cage.purchasable) "${CC.GREEN}Yes" else "${CC.RED}No"}")
            .addToLore("${CC.GRAY}Click to edit...")
            .toButton { _, _ ->
                Button.playNeutral(player)
                cage.editAndSave { purchasable = !purchasable }
                openMenu(player)
            },
        13 to ItemBuilder
            .copyOf(cage.item)
            .name("${CC.YELLOW}Item")
            .addToLore("${CC.GRAY}Click to edit...")
            .toButton { _, _ ->
                Button.playNeutral(player)
                SelectItemStackMenu("Select an item...") {
                    Button.playNeutral(player)
                    cage.editAndSave { item = it }
                    player.sendMessage("${CC.GREEN}Saved item!")
                    openMenu(player)
                }.openMenu(player)
            },
        14 to ItemBuilder
            .of(Material.MAP)
            .name("${CC.YELLOW}Schema: ${CC.WHITE}${cage.linkedSchema}")
            .addToLore("${CC.GRAY}Click to edit...")
            .toButton { _, _ ->
                Button.playNeutral(player)
                SchemaCageSelectMenu(previous = this) {
                    Button.playNeutral(player)
                    cage.editAndSave { linkedSchema = it.id }
                    player.sendMessage("${CC.GREEN}Saved schema!")
                    openMenu(player)
                }.openMenu(player)
            },
        15 to ItemBuilder
            .of(Material.ENDER_PORTAL_FRAME)
            .name("${CC.YELLOW}Rarity: ${CC.WHITE}${cage.rarity.fancy}")
            .addToLore("${CC.GRAY}Click to edit...")
            .toButton { _, _ ->
                Button.playNeutral(player)
                player.closeInventory()

                InputPrompt()
                    .withText("${CC.GREEN}Enter a new rarity (${CageRarity.entries})!")
                    .acceptInput { _, newRarity ->
                        cage.editAndSave {
                            rarity = kotlin
                                .runCatching { CageRarity.valueOf(newRarity) }
                                .getOrElse { CageRarity.Common }
                        }

                        player.sendMessage("${CC.GREEN}Saved rarity!")
                        openMenu(player)
                    }
                    .start(player)
            },
        16 to ItemBuilder
            .of(Material.BOOK)
            .name("${CC.YELLOW}Description")
            .setLore(cage.description.map { "${CC.GRAY}$it" })
            .addToLore(
                "",
                "${CC.GREEN}Click to edit..."
            )
            .toButton { _, _ ->
                Button.playNeutral(player)

                val menu = object : TextEditorMenu(cage.description)
                {
                    override fun getPrePaginatedTitle(player: Player) = "Editing cage description..."

                    override fun onClose(player: Player)
                    {
                        openMenu(player)
                    }

                    override fun onSave(player: Player, list: List<String>)
                    {
                        cage.editAndSave {
                            description = list
                        }

                        player.sendMessage("${CC.GREEN}Saved description!")
                    }

                }
                menu.openMenu(player)
            }
    )

    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (!manualClose)
        {
            return
        }

        Tasks.sync {
            CageViewAllMenu().openMenu(player)
        }
    }
}
