package land.esta.plugin.ssb.solara.freebotfridays

import gg.scala.commons.agnostic.sync.ServerSync
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
        if ("miplobby" in ServerSync.getLocalGameServer().groups)
        {
            Schedulers.async().runRepeating(
                this, 0L, 10L
            )
        }
    }

    fun getNpcEntities(): List<NpcEntity> =
        EntityHandler.getEntitiesByType(NpcEntity::class.java)

    override fun run()
    {
        val players = Bukkit.getOnlinePlayers()
        getNpcEntities()
            .forEach {
                it.hologram.updateLines(
                    handle(players.first(), it.getLines())
                )
            }
    }

    fun handle(
        player: Player,
        list: List<String>
    ): MutableList<String>
    {
        val replaced =
            mutableListOf<String>()

        for (message in list)
        {
            replaced.add(
                Replacements.apply(message, player)
            )
        }

        return replaced
    }
}
