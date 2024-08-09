package land.esta.plugin.ssb.bots

import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/9/2024
 */
fun getTargetEntity(bot: Bot) = bot.npc.bukkitEntity.world.entities
    .filterIsInstance<Player>()
    .minByOrNull {
        it.location.distanceSquared(bot.movements.location)
    }
