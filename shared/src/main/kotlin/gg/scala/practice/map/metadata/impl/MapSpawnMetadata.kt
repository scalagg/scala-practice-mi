package gg.scala.practice.map.metadata.impl

import gg.scala.practice.map.metadata.AbstractMapMetadata
import gg.scala.practice.map.metadata.anonymous.Position

/**
 * @author GrowlyX
 * @since 11/10/2022
 */
data class MapSpawnMetadata(
    override val id: String,
    var position: Position
) : AbstractMapMetadata()
{
    override fun report() = "Spawn | $id | $position"
    override fun getAbstractType() = MapSpawnMetadata::class.java
}
