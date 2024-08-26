package gg.solara.practice.minigame.skywars.shared.profile

import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
data class SkyWarsProfile(
    override val identifier: UUID,
    var selectedKit: String? = null
) : IDataStoreObject
{
    fun save() = DataStoreObjectControllerCache
        .findNotNull<SkyWarsProfile>()
        .save(this, DataStoreStorageType.MONGO)
}
