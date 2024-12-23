package gg.scala.practice.kit

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
 * @since 10/16/2022
 */
@Service
object KitService : DataSyncService<KitContainer>()
{
    object KitKeys : DataSyncKeys
    {
        override fun newStore() = "mi-practice-kits"

        override fun store() = Key.key(namespace(), "kits")
        override fun sync() = Key.key(namespaceShortened().suffixWhenDev(), "ksync")
    }

    override fun locatedIn() = DataSyncSource.Mongo

    override fun keys() = KitKeys
    override fun type() = KitContainer::class.java
}
