package gg.solara.plugin.robots.bot

import gg.solara.plugin.robots.bots.Bot

/**
 * @author GrowlyX
 * @since 8/9/2024
 */
fun getTargetEntity(bot: Bot) = bot.viewsManager.players
    .filter { !it.hasMetadata("spectator") }
    .minByOrNull {
        it.location.distanceSquared(bot.movements.location)
    }
