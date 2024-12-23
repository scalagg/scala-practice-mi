package gg.scala.practice.minigame.skywars.events

import me.lucko.helper.Helper
import net.kyori.adventure.platform.bukkit.BukkitAudiences

/**
 * @author GrowlyX
 * @since 8/26/2024
 */
object GlobalAudiences
{
    val audiences by lazy { BukkitAudiences.create(Helper.hostPlugin()) }
}
