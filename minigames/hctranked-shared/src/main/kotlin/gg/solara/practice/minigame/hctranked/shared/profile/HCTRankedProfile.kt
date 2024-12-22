package gg.solara.practice.minigame.hctranked.shared.profile

import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import gg.solara.practice.minigame.hctranked.shared.pvpclass.PvPClassType
import java.util.*

/**
 * @author Topu
 * @date 10/2/2024
 */
data class HCTRankedProfile(
    override val identifier: UUID,
    var elo: Int = 0,
    var kills: Int = 0,
    var deaths: Int = 0,
    var wins: Int = 0,
    var losses: Int = 0,
    var selectedClass: PvPClassType? = PvPClassType.DIAMOND
) : IDataStoreObject
{
    fun save() = DataStoreObjectControllerCache
        .findNotNull<HCTRankedProfile>()
        .save(this, DataStoreStorageType.MONGO)
}