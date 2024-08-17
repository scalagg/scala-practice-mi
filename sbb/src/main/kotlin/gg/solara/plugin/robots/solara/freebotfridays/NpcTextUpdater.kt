package gg.solara.plugin.robots.solara.freebotfridays

import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.replacement.ReplacementGlobalContainer
import gg.scala.commons.replacement.Replacements
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import me.lucko.helper.Schedulers
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.npc.NpcEntity
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 10/25/2021
 */
@Service
object NpcTextUpdater : Runnable
{
    @Configure
    fun configure()
    {
        Schedulers.async().runRepeating(
            this, 0L, 10L
        )
    }

    fun getNpcEntities(): List<NpcEntity> =
        EntityHandler.getEntitiesByType(NpcEntity::class.java)

    override fun run()
    {
        getNpcEntities()
            .forEach {
                it.hologram.updateLines(
                    handle(it.getLines())
                )
            }
    }

    fun handle(list: List<String>): MutableList<String>
    {
        val replaced =
            mutableListOf<String>()

        for (message in list)
        {
            replaced.add(
                message.replace(
                    "%freebotfridays-status%",
                    FreeBotFridays.fancyStatus()
                )
            )
        }

        return replaced
    }
}
