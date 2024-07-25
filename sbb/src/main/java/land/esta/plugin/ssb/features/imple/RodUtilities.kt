package land.esta.plugin.ssb.features.imple

/**
 * @author GrowlyX
 * @since 7/24/2024
 */
import net.minecraft.server.v1_8_R3.EntityHuman
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

fun getFishingRodLaunchVelocity(shooterLocation: Location, targetLocation: Location, launchSpeed: Double): Vector {
    val toTarget = targetLocation.toVector().subtract(shooterLocation.toVector())

    val normalizedDirection = toTarget.normalize()
    val launchVelocity = normalizedDirection.multiply(launchSpeed)

    return launchVelocity
}
