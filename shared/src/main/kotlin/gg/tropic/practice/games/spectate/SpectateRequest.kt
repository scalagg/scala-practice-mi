package gg.tropic.practice.games.spectate

import java.util.UUID

/**
 * @author GrowlyX
 * @since 10/20/2023
 */
@JvmRecord
data class SpectateRequest(
    val player: UUID,
    val target: UUID,
    val bypassesSpectatorAllowanceChecks: Boolean = false
)
