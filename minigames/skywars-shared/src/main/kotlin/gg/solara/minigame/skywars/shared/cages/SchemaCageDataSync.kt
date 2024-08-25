package gg.solara.minigame.skywars.shared.cages

import gg.scala.commons.persist.datasync.DataSyncKeys
import gg.scala.commons.persist.datasync.DataSyncService
import gg.scala.commons.persist.datasync.DataSyncSource
import gg.scala.flavor.service.Service
import gg.tropic.practice.namespace
import gg.tropic.practice.namespaceShortened
import gg.tropic.practice.suffixWhenDev
import net.kyori.adventure.key.Key

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
@Service
object SchemaCageDataSync : DataSyncService<SchemaCageContainer>()
{
    object SchemaCageKeys : DataSyncKeys
    {
        override fun newStore() = "schema-cages"

        override fun store() = Key.key(namespace(), "sccg")
        override fun sync() = Key.key(namespaceShortened().suffixWhenDev(), "sccgs")
    }

    override fun locatedIn() = DataSyncSource.Mongo

    override fun keys() = SchemaCageKeys
    override fun type() = SchemaCageContainer::class.java
}
