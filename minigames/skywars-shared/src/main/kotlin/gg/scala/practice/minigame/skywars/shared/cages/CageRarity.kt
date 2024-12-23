package gg.scala.practice.minigame.skywars.shared.cages

import net.evilblock.cubed.util.CC

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
enum class CageRarity(
    val fancy: String
)
{
    Common("${CC.WHITE}Common"),
    Rare("${CC.AQUA}Rare"),
    Legendary("${CC.GOLD}Legendary")
}
