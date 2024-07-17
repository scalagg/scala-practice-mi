package gg.tropic.practice.utilities

import gg.tropic.practice.games.team.GameTeamSide
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Color

/**
 * @author GrowlyX
 * @since 7/17/2024
 */
enum class RBTeamSide(
    val display: String,
    val color: String,
    val armorColor: Color
)
{
    Red(
        display = "Red",
        color = CC.RED,
        armorColor = Color.RED
    ),
    Blue(
        display = "Blue",
        color = CC.BLUE,
        armorColor = Color.BLUE
    );

    fun format(message: String) = "$color$message"
}

fun GameTeamSide.toRBTeamSide() = if (this == GameTeamSide.A)
    RBTeamSide.Red else RBTeamSide.Blue

fun ItemBuilder.toRBTeamColor(teamSide: RBTeamSide) = color(teamSide.armorColor)
