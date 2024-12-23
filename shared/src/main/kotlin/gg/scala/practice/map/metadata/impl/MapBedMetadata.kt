package gg.scala.practice.map.metadata.impl

import gg.scala.practice.map.metadata.AbstractMapMetadata
import gg.scala.practice.map.metadata.anonymous.Orientation
import gg.scala.practice.map.metadata.anonymous.Position

/**
 * @author GrowlyX
 * @since 11/10/2022
 */
data class MapBedMetadata(
    val position: Position,
    val orientation: Orientation,
    override val id: String
) : AbstractMapMetadata()
{
    override fun report() = "Bed | $id | At $position | Facing ${orientation.name}"
    override fun getAbstractType() = MapBedMetadata::class.java
}
