package gg.solara.plugin.robots.bot.behavior.actions

import gg.solara.plugin.robots.SolaraRobotsPlugin
import gg.solara.plugin.robots.bot.behavior.ActionBehavior
import gg.solara.plugin.robots.bots.Bot
import me.lucko.helper.terminable.composite.CompositeTerminable
import org.bukkit.Material

/**
 * @author TastyCake
 * @date 8/14/2024
 */
object PearlingAction : ActionBehavior
{
    override fun initialize(terminable: CompositeTerminable, bot: Bot)
    {
        val pearls = bot.inventoryManager.hasItem(Material.ENDER_PEARL)
        if (pearls.isEmpty()) {
            terminable.closeAndReportException()
            return
        }
        bot.pearlTick = 0

        val switchSlowTemp = bot.inventoryManager.switchSlowTemp(pearls.first())
        SolaraRobotsPlugin.instance!!.featureManager.pearling.pearl(pearls.first(), terminable, bot, switchSlowTemp)
    }

    override fun tick(terminable: CompositeTerminable, bot: Bot)
    {

    }

    override fun complete(bot: Bot)
    {

    }
}
