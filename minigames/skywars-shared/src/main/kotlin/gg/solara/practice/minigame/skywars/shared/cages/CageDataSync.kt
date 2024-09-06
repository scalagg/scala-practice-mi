package gg.solara.practice.minigame.skywars.shared.cages

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
object CageDataSync : DataSyncService<CageContainer>()
{
    object CageKeys : DataSyncKeys
    {
        override fun newStore() = "cages"

        override fun store() = Key.key(namespace(), "cg")
        override fun sync() = Key.key(namespaceShortened().suffixWhenDev(), "cgs")
    }

    override fun locatedIn() = DataSyncSource.Mongo

    override fun keys() = CageKeys
    override fun type() = CageContainer::class.java
}
