package gg.scala.practice.profile

import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.practice.divisions.Divisions
import gg.scala.practice.kit.Kit
import gg.scala.practice.profile.loadout.Loadout
import gg.scala.practice.profile.ranked.RankedBan
import gg.scala.practice.statistics.GlobalStatistics
import gg.scala.practice.statistics.KitStatistics
import gg.scala.practice.statistics.UnrankedBotKitStatistics
import gg.scala.practice.statistics.ranked.RankedKitStatistics
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.time.Duration
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author GrowlyX
 * @since 9/17/2023
 */
data class PracticeProfile(
    override val identifier: UUID
) : IDataStoreObject
{
    private var rankedBan: RankedBan? = null
    private var favoriteKits = mutableListOf<Kit>()

    var globalStatistics = GlobalStatistics()
    val unrankedStatistics = ConcurrentHashMap<
        String,
        KitStatistics
    >()

    val rankedStatistics = ConcurrentHashMap<
        String,
        RankedKitStatistics
    >()

    var botStatistics = ConcurrentHashMap<
        String,
        UnrankedBotKitStatistics
    >()

    val customLoadouts = mutableMapOf<
        String,
        MutableList<
            Loadout
        >
    >()

    var botDivision: String? = Divisions.NONE.id

    fun getUnrankedStatsFor(kit: Kit) = unrankedStatistics
        .putIfAbsent(
            kit.id, KitStatistics()
        )
        ?: unrankedStatistics[kit.id]!!

    fun getRankedStatsFor(kit: Kit) = rankedStatistics
        .putIfAbsent(
            kit.id, RankedKitStatistics()
        )
        ?: rankedStatistics[kit.id]!!

    fun getUnrankedBotStatsFor(kit: Kit) : UnrankedBotKitStatistics
    {
        return botStatistics?.putIfAbsent(
            kit.id, UnrankedBotKitStatistics(),
        )
        ?: botStatistics?.get(kit.id) ?: run {
            botStatistics = ConcurrentHashMap()
            botStatistics.putIfAbsent(kit.id, UnrankedBotKitStatistics())
            save()
            botStatistics[kit.id]!!
        }
    }

    fun getLoadoutsFromKit(kit: Kit) = customLoadouts[kit.id] ?: mutableListOf()

    fun hasActiveRankedBan() = rankedBan != null && rankedBan!!.isEffective()

    fun applyRankedBan(duration: Duration)
    {
        val banDuration = if (duration.isPermanent()) null else System.currentTimeMillis() + duration.get()

        rankedBan = RankedBan(
            effectiveUntil = banDuration
        )
    }

    fun deliverRankedBanMessage(player: Player) = rankedBan!!.deliverBanMessage(player)

    fun rankedBanEffectiveUntil() = rankedBan!!.effectiveUntil
    fun removeRankedBan()
    {
        rankedBan = null
    }

    fun onUnrankedBotWin() {
        val div = Divisions.entries.firstOrNull { it.requiredWins == globalStatistics.totalUnrankedBotWins }
        if (div != null) {
            Bukkit.getPlayer(identifier).sendMessage("${CC.GREEN}You were granted a new division! ${div.title}")
            botDivision = div.name
        }

        save()
    }

    fun save() = DataStoreObjectControllerCache
        .findNotNull<PracticeProfile>()
        .save(this)
}
