package gg.scala.practice.minigame.skywars.shared.kits

import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
interface SkyWarsKit
{
    val name: String
    val icon: ItemBuilder

    fun applyArmor(player: Player)
    fun applyInventoryContents(player: Player)
}
