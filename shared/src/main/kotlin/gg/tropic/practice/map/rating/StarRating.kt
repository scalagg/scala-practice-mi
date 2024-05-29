package gg.tropic.practice.map.rating

import org.bukkit.ChatColor

/**
 * @author GrowlyX
 * @since 5/28/2024
 */
enum class StarRating(val color: ChatColor)
{
    One(ChatColor.RED), Two(ChatColor.GOLD), Three(ChatColor.YELLOW), Four(ChatColor.YELLOW), Five(ChatColor.GREEN)
}
