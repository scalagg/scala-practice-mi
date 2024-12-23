package gg.scala.practice.games.bots

import gg.scala.practice.namespace
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.serializers.Serializers
import java.util.UUID

/**
 * @author GrowlyX
 * @since 8/9/2024
 */
fun String.extractBotGameMetadata() = Serializers.gson.fromJson(this, BotGameMetadata::class.java)

fun getBotMetdataOfPlayer(player: UUID) = ScalaCommonsSpigot.instance.kvConnection.sync()
    .get(
        "${namespace()}:bot-metadata:$player"
    )
    .extractBotGameMetadata()

fun BotGameMetadata.storeForUser(player: UUID) = ScalaCommonsSpigot.instance.kvConnection.sync()
    .set(
        "${namespace()}:bot-metadata:$player",
        Serializers.gson.toJson(this)
    )
