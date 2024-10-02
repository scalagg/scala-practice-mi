package gg.solara.practice.minigame.hctranked.shared.profile

import gg.scala.commons.persist.ProfileOrchestrator
import gg.scala.flavor.service.Service
import java.util.*

/**
 * @author Topu
 * @date 10/2/2024
 */
@Service
object HCTRankedProfileService : ProfileOrchestrator<HCTRankedProfile>()
{
    override fun new(uniqueId: UUID) = HCTRankedProfile(uniqueId)
    override fun type() = HCTRankedProfile::class
}