package gg.solara.practice.minigame.hctranked.shared.pvpclass.classes.effect

import gg.scala.flavor.service.Service
import org.bukkit.potion.PotionEffect

/**
 * @author Topu
 * @date 10/5/2024
 */
data class BardEffect(
    val potionEffect: PotionEffect?,
    val energy: Int = 0
)
{
    val lastMessageSent: MutableMap<String, Long> = HashMap()

    companion object
    {
        val lastMessageSent: MutableMap<String, Long> = HashMap()
        const val MESSAGE_COOLDOWN = 5000L

        fun fromPotion(potionEffect: PotionEffect): BardEffect
        {
            return BardEffect(potionEffect, -1)
        }

        fun fromPotionAndEnergy(potionEffect: PotionEffect, energy: Int): BardEffect
        {
            return BardEffect(potionEffect, energy)
        }

        fun fromEnergy(energy: Int): BardEffect
        {
            return BardEffect(null, energy)
        }

        fun shouldSendMessage(playerName: String): Boolean
        {
            val currentTime = System.currentTimeMillis()
            val lastSent = lastMessageSent[playerName] ?: 0
            return (currentTime - lastSent) > MESSAGE_COOLDOWN
        }

        fun updateMessageTime(playerName: String)
        {
            lastMessageSent[playerName] = System.currentTimeMillis()
        }

    }
}