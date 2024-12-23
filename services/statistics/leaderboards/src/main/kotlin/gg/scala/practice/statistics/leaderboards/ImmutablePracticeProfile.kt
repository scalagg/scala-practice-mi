package gg.scala.practice.statistics.leaderboards

import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.practice.statistics.GlobalStatistics
import gg.scala.practice.statistics.KitStatistics
import gg.scala.practice.statistics.UnrankedBotKitStatistics
import gg.scala.practice.statistics.ranked.RankedKitStatistics
import java.util.*

/**
 * @author GrowlyX
 * @since 12/16/2023
 */
data class ImmutablePracticeProfile(
    override val identifier: UUID
) : IDataStoreObject
{
    val globalStatistics: GlobalStatistics = GlobalStatistics()

    val unrankedStatistics: Map<String, KitStatistics> = mapOf()
    val rankedStatistics: Map<String, RankedKitStatistics> = mapOf()
    val unrankedBotStatistics: Map<String, UnrankedBotKitStatistics> = mapOf()
}
