package gg.tropic.practice.map.metadata.scanner.impl

import gg.tropic.practice.map.metadata.anonymous.Orientation
import gg.tropic.practice.map.metadata.anonymous.toPosition
import gg.tropic.practice.map.metadata.impl.MapBedMetadata
import gg.tropic.practice.map.metadata.scanner.AbstractMapMetadataScanner
import gg.tropic.practice.map.metadata.sign.MapSignMetadataModel
import org.bukkit.material.Sign

/**
 * @author GrowlyX
 * @since 11/10/2022
 */
object MapBedMetadataScanner : AbstractMapMetadataScanner<MapBedMetadata>()
{
    override val type = "bed"

    override fun scan(
        id: String,
        models: List<MapSignMetadataModel>
    ): MapBedMetadata
    {
        val model = models.first()

        val position = model.location.clone().toPosition()
        val sign = model.location.block.state.data as Sign
        val orientation = runCatching { Orientation.valueOf(sign.facing.name) }
            .getOrElse { Orientation.NORTH }

        return MapBedMetadata(
            position, orientation, id
        )
    }
}
