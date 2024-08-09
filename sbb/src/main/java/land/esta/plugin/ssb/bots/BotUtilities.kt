package land.esta.plugin.ssb.bots

/**
 * @author GrowlyX
 * @since 8/9/2024
 */
fun getTargetEntity(bot: Bot) = bot.viewsManager.players
    .minByOrNull {
        it.location.distanceSquared(bot.movements.location)
    }
