package gg.tropic.practice.commands

import gg.scala.commons.acf.CommandHelp
import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandCompletion
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Default
import gg.scala.commons.acf.annotation.Description
import gg.scala.commons.acf.annotation.HelpCommand
import gg.scala.commons.acf.annotation.Single
import gg.scala.commons.acf.annotation.Subcommand
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.tropic.practice.kit.KitService
import gg.tropic.practice.kit.category.KitCategory
import gg.tropic.practice.kit.category.KitCategoryService
import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.CC
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 2/18/2023
 */
@AutoRegister
@CommandPermission("practice.command.kitcategory")
@CommandAlias("kitcategory|kitcategories|kc")
object KitCategoryCommand : ScalaCommand()
{

    @Default
    @HelpCommand
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }

    @Subcommand("list")
    @Description("List all categories.")
    fun onList(player: ScalaPlayer)
    {
        player.sendMessage("=== categories ===")

        for (category in KitCategoryService.cached().categories)
        {
            player.sendMessage(category.id)
        }
    }

    @Subcommand("show")
    @CommandCompletion("@categories")
    @Description("Show a category.")
    fun onMetaShow(
        player: ScalaPlayer, category: KitCategory
    )
    {
        player.sendMessage(
            "${CC.PRI}${category.id}",
            "${CC.GRAY}Name: ${CC.WHITE}${category.name}",
            "${CC.GRAY}New: ${CC.WHITE}${category.newCategory}",
            "${CC.GRAY}Item: ${CC.WHITE}${category.item.name}",
            "",
            "${CC.PRI}Description:",
            *category.description.toTypedArray(),
            "",
            "${CC.PRI}Kit bindings:",
            *KitService.cached().kits.values
                .filter { it.kitCategory == category.id }
                .map { " - " + it.displayName }
                .toTypedArray()
        )
    }

    @Subcommand("displayicon")
    @CommandCompletion("@categories")
    @Description("Set a category's material.")
    fun onMetaMaterial(
        player: ScalaPlayer, category: KitCategory, material: Material
    )
    {
        category.item = material

        KitCategoryService.sync(
            KitCategoryService.cached()
        )

        player.sendMessage(
            "${CC.GREEN}You set the material for ${CC.YELLOW}${category.id}${CC.GREEN} to ${CC.WHITE}${material.name}${CC.GREEN}!"
        )
    }

    @Subcommand("displayname")
    @CommandCompletion("@categories")
    @Description("Set a category's display name.")
    fun onMetaName(
        player: ScalaPlayer, category: KitCategory, name: String
    )
    {
        category.name = name

        KitCategoryService.sync(
            KitCategoryService.cached()
        )

        player.sendMessage(
            "${CC.GREEN}You set the name for ${CC.YELLOW}${category.id}${CC.GREEN} to ${CC.WHITE}${category.name}${CC.GREEN}!"
        )
    }

    @Subcommand("description")
    @CommandCompletion("@categories")
    @Description("Edit a category's description.")
    fun onMetaDescription(
        player: ScalaPlayer, category: KitCategory
    )
    {
        object : TextEditorMenu(category.description)
        {
            override fun getPrePaginatedTitle(player: Player) =
                "Edit description for: ${category.id}"

            override fun onClose(player: Player)
            {

            }

            override fun onSave(player: Player, list: List<String>)
            {
                category.description = list.toMutableList()

                KitCategoryService.sync(
                    KitCategoryService.cached()
                )

                player.sendMessage(
                    "${CC.GREEN}You updated the description for ${CC.YELLOW}${category.id}${CC.GREEN}!"
                )
            }
        }.openMenu(player.bukkit())
    }

    @Subcommand("newcategory")
    @CommandCompletion("@categories")
    @Description("Set a category as new.")
    fun onMetaNewCategory(
        player: ScalaPlayer, category: KitCategory
    )
    {
        category.newCategory = !category.newCategory

        KitCategoryService.sync(
            KitCategoryService.cached()
        )

        player.sendMessage(
            "${CC.GREEN}You ${
                if (category.newCategory) "${CC.D_GREEN}enabled" else "${CC.RED}disabled"
            }${CC.GREEN} new mode for ${CC.YELLOW}${category.id}${CC.GREEN}!"
        )
    }

    @Subcommand("delete")
    @CommandCompletion("@categories")
    @Description("Delete a kit category.")
    fun onCreate(player: ScalaPlayer, category: KitCategory)
    {
        val model = KitCategoryService.cached()
        model.categories -= category

        KitCategoryService.sync(model)

        player.sendMessage(
            "${CC.RED}You removed the kit category ${CC.D_RED}$name${CC.RED}!"
        )
    }

    @Subcommand("create")
    @Description("Create a new kit category.")
    fun onCreate(player: ScalaPlayer, @Single name: String)
    {
        if (KitCategoryService.cached().findById(name) != null)
        {
            throw ConditionFailedException(
                "A kit category by that name already exists!"
            )
        }

        val model = KitCategoryService.cached()
        model.categories += KitCategory(name)

        KitCategoryService.sync(model)

        player.sendMessage(
            "${CC.GREEN}You created a new kit category with the name ${CC.D_GREEN}$name${CC.GREEN}!"
        )
    }
}