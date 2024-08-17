package gg.solara.plugin.robots.features.impl

import gg.solara.plugin.robots.bots.Bot
import gg.solara.plugin.robots.features.Feature

/**
 * @author TastyCake
 * @date 8/13/2024
 */
class SuffocationFeature : Feature()
{
    override fun tickEvent(bot: Bot)
    {
        val location = bot.movements.location
        val location2 = location.clone().add(0.0, 1.0, 0.0)

        if ((location.block.type.isSolid
            && location.block.type.isOccluding)
            || (location2.block.type.isSolid
            && location2.block.type.isOccluding)
        ) {
            if (System.currentTimeMillis() - 500 > bot.lastSuffocationDamage) {
                bot.damageManager.playSound()
                bot.damageManager.damageEffect()
                bot.healthManager.removeHealth(1.0)
                bot.lastSuffocationDamage = System.currentTimeMillis()
            }
        }
    }
}
