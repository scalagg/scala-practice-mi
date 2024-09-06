package gg.solara.plugin.robots.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.Single
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
object UnrankedAdminCommand : ScalaCommand()
{
    @CommandAlias("unrankedadmin|ura")
    fun onUnrankedAdmin(player: ScalaPlayer, @Single arg: String) {
        if (player.bukkit().isOp) {
            if (arg.equals("fight", ignoreCase = true)) {
                val kit = KitService.cached().kits["sumo"]!!
                player.bukkit().closeInventory()
                CompletableFuture.runAsync {
                    val instances = mutableSetOf<UUID>()
                    repeat(1) {
                        instances += Globals.POSSIBLE_PLAYER_BOT_UNIQUE_IDS[it]
                    }

                    BotGameMetadata("UNRANKED", UnrankedBotsService.bots.values.random().settingProfile.id, kit.id, null, botInstances = instances)
                        .storeForUser(player.uniqueId)
                }.thenAccept {
                    QueueService.joinQueue(kit, QueueType.Robot, 1, player.bukkit())
                    Button.playNeutral(player.bukkit())

                    player.sendMessage(
                        "${CC.GREEN}Preparing your ${CC.PRI}${QueueType.Robot.name} ${kit.displayName}${CC.GREEN} match..."
                    )
                }.exceptionally {
                    it.printStackTrace()
                    player.sendMessage("${CC.RED}We weren't able to create a bot fight for you. Please report this to an administrator!")
                    return@exceptionally null
                }
            }
            else UnrankedAdminMenu().openMenu(player.bukkit())
        }
    }
}