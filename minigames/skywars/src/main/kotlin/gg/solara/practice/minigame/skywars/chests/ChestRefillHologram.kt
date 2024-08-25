package gg.solara.practice.minigame.skywars.chests

import gg.solara.practice.minigame.skywars.SkyWarsLifecycle
import gg.solara.practice.minigame.skywars.events.GameEvent
import me.lucko.helper.Helper
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.hologram.updating.UpdatingHologramEntity
import net.evilblock.cubed.util.CC
import org.bukkit.Location
import org.bukkit.block.Chest
import org.bukkit.metadata.FixedMetadataValue

class ChestRefillHologram(
    private val lifecycle: SkyWarsLifecycle,
    private val associatedEvent: GameEvent,
    location: Location
) : UpdatingHologramEntity(
    "", location
), CompositeTerminable by CompositeTerminable.create()
{
    override fun close()
    {
        ChestPacketCreator.closeChest(location)

        location.block.removeMetadata("hologram", Helper.hostPlugin())

        destroyForCurrentWatchers()
        EntityHandler.forgetEntity(this)
    }

    fun create()
    {
        ChestPacketCreator.openChest(location)
        location.block.setMetadata("hologram", FixedMetadataValue(Helper.hostPlugin(), this))

        initializeData()
        EntityHandler.trackEntity(this)
    }

    override fun getNewLines(): List<String>
    {
        val chest = location.block.state as Chest

        if (chest.inventory.contents.all { it == null })
        {
            return listOf("${CC.RED}Empty!", "", "${CC.GREEN}${associatedEvent.formattedEstimatedExecution()}")
        }

        return listOf(
            "${CC.GREEN}${associatedEvent.formattedEstimatedExecution()}"
        )
    }

    override fun getTickInterval() = 500L
}
