package gg.solara.plugin.robots.bot.behavior.actions

import gg.solara.plugin.robots.bot.behavior.ActionBehavior
import gg.solara.plugin.robots.bots.Bot
import me.lucko.helper.terminable.composite.CompositeTerminable

/**
 * @author TastyCake
 * @date 8/14/2024
 */
object RodAction : ActionBehavior
{
    override fun initialize(terminable: CompositeTerminable, bot: Bot)
    {


        terminable.closeAndReportException()
    }

    override fun tick(terminable: CompositeTerminable, bot: Bot)
    {

    }

    override fun complete(bot: Bot)
    {

    }
}