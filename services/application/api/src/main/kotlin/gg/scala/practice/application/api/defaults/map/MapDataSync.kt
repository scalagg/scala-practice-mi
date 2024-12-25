package gg.scala.practice.application.api.defaults.map

import gg.scala.practice.application.api.DPSDataSync
import gg.scala.practice.application.api.DPSDataSyncKeys
import gg.scala.practice.application.api.DPSDataSyncSource
import gg.scala.practice.application.api.defaults.kit.ImmutableKit
import gg.scala.practice.application.api.defaults.kit.group.ImmutableKitGroup
import gg.scala.practice.application.api.defaults.kit.group.KitGroupDataSync
import gg.scala.practice.namespace
import gg.scala.practice.namespaceShortened
import gg.scala.practice.suffixWhenDev
import net.kyori.adventure.key.Key

/**
 * @author GrowlyX
 * @since 9/24/2023
 */
object MapDataSync : DPSDataSync<ImmutableMapContainer>()
{
    object DPSMapKeys : DPSDataSyncKeys
    {
        override fun newStore() = "mi-practice-maps"

        override fun store() = Key.key(namespace(), "maps")
        override fun sync() = Key.key(namespaceShortened().suffixWhenDev(), "msync")
    }

    override fun locatedIn() = DPSDataSyncSource.Mongo

    override fun keys()  = DPSMapKeys
    override fun type()  = ImmutableMapContainer::class.java

    fun selectMapIfCompatible(kit: ImmutableKit, mapID: String?): ImmutableMap?
    {
        val groups = KitGroupDataSync.groupsOf(kit)
            .map(ImmutableKitGroup::id)

        return cached().maps.values
            .filterNot(ImmutableMap::locked)
            .filter { groups.intersect(it.associatedKitGroups).isNotEmpty() }
            .shuffled()
            .firstOrNull { if (mapID == null) true else it.name == mapID }
    }

    fun selectRandomMapCompatibleWith(kit: ImmutableKit): ImmutableMap?
    {
        val groups = KitGroupDataSync.groupsOf(kit)
            .map(ImmutableKitGroup::id)

        return cached().maps.values
            .filterNot(ImmutableMap::locked)
            .shuffled()
            .firstOrNull {
                groups.intersect(it.associatedKitGroups).isNotEmpty()
            }
    }
}
