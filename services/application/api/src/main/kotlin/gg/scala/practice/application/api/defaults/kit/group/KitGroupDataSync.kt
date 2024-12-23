package gg.scala.practice.application.api.defaults.kit.group

import gg.scala.practice.application.api.DPSDataSync
import gg.scala.practice.application.api.DPSDataSyncKeys
import gg.scala.practice.application.api.DPSDataSyncSource
import gg.scala.practice.application.api.defaults.kit.ImmutableKit
import gg.scala.practice.namespace
import gg.scala.practice.namespaceShortened
import gg.scala.practice.suffixWhenDev
import net.kyori.adventure.key.Key

/**
 * @author GrowlyX
 * @since 9/24/2023
 */
object KitGroupDataSync : DPSDataSync<ImmutableKitContainer>()
{
    object DPSKitGroupKeys : DPSDataSyncKeys
    {
        override fun newStore() = "mi-practice-kits-groups"

        override fun store() = Key.key(namespace(), "groups")
        override fun sync() = Key.key(namespaceShortened().suffixWhenDev(), "gsync")
    }

    override fun locatedIn() = DPSDataSyncSource.Mongo

    override fun keys() = DPSKitGroupKeys
    override fun type() = ImmutableKitContainer::class.java

    fun groupsOf(kit: ImmutableKit) = cached().backingGroups
        .filter {
            kit.id in it.contains
        }
}
