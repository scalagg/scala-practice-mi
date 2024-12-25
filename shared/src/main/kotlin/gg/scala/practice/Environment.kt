package gg.scala.practice

import org.bukkit.Bukkit

/**
 * @author GrowlyX
 * @since 2/10/2024
 */
var devProvider: () -> Boolean = { false }
var isSmartBotsEnabled = Bukkit.getPluginManager().isPluginEnabled("Practice-SmartBots")

fun isDev() = devProvider()
fun isProd() = !devProvider()

fun namespace() = "scalapractice"
fun namespaceShortened() = "tp"

fun practiceGroup() = "mip"
fun gameGroup() = "mipgame"
fun lobbyGroup() = "miplobby"

fun String.suffixWhenDev() = (if (isDev()) "${if (this == "scalapractice")
    "scalaprac" else this}dev" else this)
