package gg.tropic.practice.games.robot

import gg.tropic.practice.games.GameImpl
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 7/20/2024
 */
object RobotGameLifecycle
{
    var createNewRobotInstance: (GameImpl) -> CompletableFuture<RobotInstance?> = {
        throw UnsupportedOperationException("Robot integration not available")
    }
}
