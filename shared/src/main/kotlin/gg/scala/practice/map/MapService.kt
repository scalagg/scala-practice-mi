package gg.scala.practice.map

import gg.scala.commons.persist.datasync.DataSyncKeys
import gg.scala.commons.persist.datasync.DataSyncService
import gg.scala.commons.persist.datasync.DataSyncSource
import gg.scala.flavor.service.Service
import gg.scala.practice.PracticeShared
import gg.scala.practice.namespace
import gg.scala.practice.namespaceShortened
import gg.scala.practice.suffixWhenDev
import net.kyori.adventure.key.Key

/**
 * @author GrowlyX
 * @since 9/21/2023
 */
@Service
object MapService : DataSyncService<MapContainer>()
{
    object MapKeys : DataSyncKeys
    {
        override fun newStore() = "mi-practice-maps"

        override fun store() = Key.key(namespace(), "maps")
        override fun sync() = Key.key(namespaceShortened().suffixWhenDev(), "msync")
    }

    override fun locatedIn() = DataSyncSource.Mongo

    override fun keys() = MapKeys
    override fun type() = MapContainer::class.java

    fun maps() = cached().maps.values

    fun mapWithID(id: String) = cached().maps.values
        .firstOrNull {
            it.name.equals(id, true)
        }

    fun mapWithSlime(id: String) = cached().maps.values
        .firstOrNull {
            it.associatedSlimeTemplate.equals(id, true)
        }

    var onPostReload = {}
    override fun postReload()
    {
        onPostReload()
    }
}
