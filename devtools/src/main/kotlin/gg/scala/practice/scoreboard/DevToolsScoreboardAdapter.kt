package gg.scala.practice.scoreboard

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.LemonConstants
import gg.scala.practice.editor.Editor
import gg.scala.practice.editor.mapeditor.MapEditor
import gg.scala.practice.map.MapService
import gg.scala.practice.region.PlayerRegionFromRedisProxy
import gg.scala.practice.services.ScoreboardTitleService
import gg.scala.practice.settings.layout
import gg.scala.practice.settings.scoreboard.ScoreboardStyle
import me.lucko.helper.Events
import me.lucko.helper.Helper
import net.evilblock.cubed.scoreboard.ScoreboardAdapter
import net.evilblock.cubed.scoreboard.ScoreboardAdapterRegister
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.math.Numbers
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

/**
 * @author Topu
 * @date 1/18/2025
 */
@Service
@ScoreboardAdapterRegister
object DevToolsScoreboardAdapter : ScoreboardAdapter()
{
    override fun getLines(board: LinkedList<String>, player: Player)
    {
        board += ""
        board += "${CC.WHITE}Online: ${CC.PRI}${
            Bukkit.getOnlinePlayers().size
        }"
        board += "${CC.WHITE}Maps: ${CC.PRI}${
            Numbers.format(MapService.maps().size)
        }"

        if (Editor.instances[player.uniqueId] != null || MapEditor.instances[player.uniqueId] != null)
        {
            board += ""
            board += "${CC.GOLD}Dev Tools:"
            board += "${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.WHITE} Schematics: ${CC.GREEN}${
                if (MapEditor.instances[player.uniqueId] == null)
                {
                    Editor.instances[player.uniqueId]!!.schematics.size
                } else
                {
                    MapEditor.instances[player.uniqueId]!!.schematics.size
                }
            }"
            board += "${CC.GRAY}${Constants.THIN_VERTICAL_LINE}${CC.WHITE} Visiting: ${CC.PRI}${
                if (Editor.instances[player.uniqueId]?.visiting?.associatedElement != null) {
                    Editor.instances[player.uniqueId]!!.visiting!!.associatedElement.nameWithoutExtension
                } else {
                    "N/A"
                }
            }"
        }

        board += ""
        board += CC.GRAY + LemonConstants.WEB_LINK + "          " + CC.GRAY + "      " + CC.GRAY + "  " + CC.GRAY
    }

    @Configure
    fun configure()
    {
        Events
            .subscribe(PlayerJoinEvent::class.java)
            .handler {
                PlayerRegionFromRedisProxy.of(it.player)
                    .thenAccept { region ->
                        it.player.setMetadata(
                            "region",
                            FixedMetadataValue(Helper.hostPlugin(), region.name)
                        )
                    }
            }

        Events
            .subscribe(PlayerQuitEvent::class.java)
            .handler {
                it.player.removeMetadata("region", Helper.hostPlugin())
            }
    }

    override fun getTitle(player: Player) = if (layout(player) == ScoreboardStyle.Default)
        ScoreboardTitleService.getCurrentTitle() else "${CC.B_PRI}Practice"
}