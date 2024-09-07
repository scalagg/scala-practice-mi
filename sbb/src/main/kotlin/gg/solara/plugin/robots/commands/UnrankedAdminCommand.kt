package gg.solara.plugin.robots.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Default
import gg.scala.commons.acf.annotation.Single
import gg.scala.commons.acf.annotation.Subcommand
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.solara.plugin.robots.bot.menu.UnrankedAdminMenu
import gg.solara.plugin.robots.bot.profile.UnrankedBotsService
import gg.tropic.practice.Globals
import gg.tropic.practice.games.bots.BotGameMetadata
import gg.tropic.practice.games.bots.storeForUser
import gg.tropic.practice.kit.KitService
import gg.tropic.practice.queue.QueueService
import gg.tropic.practice.queue.QueueType
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

/**
 * @author TastyCake
 * @date 8/29/2024
 */
@AutoRegister
@CommandAlias("unrankedadmin|ura")
@CommandPermission("practice.bot.unrankedadmin")
object UnrankedAdminCommand : ScalaCommand()
{
    var available = true

    @Default
    fun onUnrankedAdmin(player: ScalaPlayer) {
        UnrankedAdminMenu().openMenu(player.bukkit())
    }

    @Subcommand("toggle")
    fun onUnrankedBotToggle(player: ScalaPlayer) {
        available = !available
        player.sendMessage("${CC.GREEN}Toggled to: $available")
    }
}