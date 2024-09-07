package gg.tropic.practice.profile

import gg.scala.commons.persist.ProfileOrchestrator
import gg.scala.flavor.service.Service
import gg.tropic.practice.divisions.DivisionMenu
import gg.tropic.practice.divisions.Divisions
import gg.tropic.practice.kit.KitService
import gg.tropic.practice.statistics.KitStatistics
import gg.tropic.practice.statistics.ranked.RankedKitStatistics
import net.evilblock.cubed.util.bukkit.Tasks
import java.util.*

/**
 * @author GrowlyX
 * @since 9/17/2023
 */
@Service
object PracticeProfileService : ProfileOrchestrator<PracticeProfile>()
{
    override fun new(uniqueId: UUID) = PracticeProfile(uniqueId)
    override fun type() = PracticeProfile::class

    override fun postLoad(uniqueId: UUID)
    {
        Tasks
            .delayed(20) {
                val profile = find(uniqueId)
                    ?: return@delayed

                val unrankedBotWins = profile.unrankedBotWins?: 0
                var highestDiv = Divisions.NONE

                Divisions.entries.forEach {
                    if (unrankedBotWins < it.requiredWins) {
                        return@delayed
                    }
                    if (it.requiredWins > highestDiv.requiredWins) {
                        highestDiv = it
                    }
                }

                profile.botDivision = highestDiv.name
                profile.save()
            }

        val profile = find(uniqueId)
            ?: return

        var hasAnyUpdates = false

        KitService.cached().kits.values
            .forEach {
                val requiresUpdates = listOf(
                    profile.rankedStatistics.putIfAbsent(it.id, RankedKitStatistics()),
                    profile.casualStatistics.putIfAbsent(it.id, KitStatistics()),
                    profile.customLoadouts.putIfAbsent(it.id, mutableListOf())
                ).any { update -> update == null }

                if (requiresUpdates)
                {
                    hasAnyUpdates = true
                }
            }

        if (hasAnyUpdates)
        {
            profile.save()
        }
    }
}
