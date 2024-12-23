package gg.scala.practice.minigame.skywars.shared.profile

import gg.scala.commons.persist.ProfileOrchestrator
import gg.scala.flavor.service.Service
import java.util.*

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
@Service
object SkyWarsProfileService : ProfileOrchestrator<SkyWarsProfile>()
{
    override fun new(uniqueId: UUID) = SkyWarsProfile(uniqueId)
    override fun type() = SkyWarsProfile::class
}
