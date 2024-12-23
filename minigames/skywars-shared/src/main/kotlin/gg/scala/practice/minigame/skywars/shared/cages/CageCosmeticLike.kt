package gg.scala.practice.minigame.skywars.shared.cages

import gg.scala.game.extensions.cosmetics.CosmeticLike

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
interface CageCosmeticLike : CosmeticLike
{
    val cage: Cage
}
