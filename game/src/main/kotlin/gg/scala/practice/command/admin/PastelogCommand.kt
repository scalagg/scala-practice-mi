package gg.scala.practice.command.admin

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.commons.plugin.PluginBridge
import gg.scala.flavor.inject.Inject
import gg.scala.practice.PracticeGame
import gg.scala.practice.PracticeShared
import net.evilblock.cubed.util.CC
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * @author TastyCake
 * @date 9/8/2024
 */
@AutoRegister
object PastelogCommand : ScalaCommand()
{
    @Inject
    lateinit var plugin: PracticeGame

    @CommandAlias("pastelog")
    @CommandPermission("pla.pastelog")
    fun onPasteLog(player: ScalaPlayer) {
        try {
            val pluginFolder = plugin.dataFolder
            val serverRoot = pluginFolder.parentFile.parentFile

            val logFilePath = File(serverRoot, "logs/latest.log").absolutePath

            val command = arrayOf("sh", "-c", "curl -T $logFilePath https://paste.lunarlabs.cc/data/post")

            val process = Runtime.getRuntime().exec(command)
            val reader = BufferedReader(InputStreamReader(process.inputStream))

            val pasteUrl = reader.readLine()

            player.sendMessage("${CC.GREEN}Log pasted to $pasteUrl")

            process.waitFor()
            reader.close()
        } catch (e: Exception) {
            player.sendMessage("${CC.RED}Failed to paste log: ${e.message}")
        }
    }
}