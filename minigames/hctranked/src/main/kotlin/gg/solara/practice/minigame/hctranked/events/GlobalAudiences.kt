package gg.solara.practice.minigame.hctranked.events

import me.lucko.helper.Helper
import net.kyori.adventure.platform.bukkit.BukkitAudiences

/**
 * @author Topu
 * @date 10/2/2024
 */
object GlobalAudiences
{
    val audiences by lazy { BukkitAudiences.create(Helper.hostPlugin()) }
}