package gg.solara.practice.minigame.skywars.chests

import gg.solara.practice.minigame.skywars.SkyWarsLifecycle
import gg.solara.practice.minigame.skywars.events.ChestRefillEvent
import me.lucko.helper.Helper
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.hologram.updating.UpdatingHologramEntity
import net.evilblock.cubed.util.CC
import org.bukkit.Location
import org.bukkit.block.Chest
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock
import org.bukkit.craftbukkit.v1_8_R3.block.CraftChest
import org.bukkit.metadata.FixedMetadataValue

class ChestRefillHologram(
    private val lifecycle: SkyWarsLifecycle,
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
        if (chest.inventory.contents.isEmpty())
        {
            println(chest.inventory.contents)
            return listOf("${CC.RED}Empty!")
        }

        val currentEvent = lifecycle.events.firstOrNull()
        if (currentEvent !is ChestRefillEvent)
        {
            return listOf("")
        }

        return listOf(
            "${CC.RED}Refill",
            "${CC.GREEN}${currentEvent.formattedEstimatedExecution()}"
        )
    }

    override fun getTickInterval() = 500L
}
