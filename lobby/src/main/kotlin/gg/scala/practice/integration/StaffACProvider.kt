package gg.scala.practice.integration

import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.scala.staff.anticheat.AnticheatFeature

/**
 * @author GrowlyX
 * @since 1/16/2024
 */
@Service
@IgnoreAutoScan
@SoftDependency("ScStaff")
object StaffACProvider
{
    @Configure
    fun configure()
    {
        // disable the anticheat in practice lobbies
        AnticheatFeature.proxiedPlayerAllowAC = context@{ false }
    }
}
