package gg.scala.practice.utilities

import gg.scala.practice.games.AbstractGame
import gg.scala.practice.games.team.GameTeam
import gg.scala.practice.games.team.TeamIdentifier
import gg.scala.practice.kit.feature.FeatureFlag
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
    val armorColor: Color,
    val blockColor: Short
)
{
    Red(
        display = "Red",
        color = CC.RED,
        armorColor = Color.RED,
        blockColor = 14
    ),
    Blue(
        display = "Blue",
        color = CC.BLUE,
        armorColor = Color.BLUE,
        blockColor = 11
    );

    fun format(message: String) = "$color$message"
}

fun TeamIdentifier.toRBTeamSide() = if (this == TeamIdentifier.A)
    RBTeamSide.Red else RBTeamSide.Blue

// TODO: update colors to be compat with A/B/C/D/... (potentially Hypixel colors for bedwars?)
fun AbstractGame.colorOf(team: GameTeam) = if (kit.features(FeatureFlag.RedBlueTeams))
{
    if (team.teamIdentifier == TeamIdentifier.A)
        CC.RED else CC.BLUE
} else
{
    if (team.teamIdentifier == TeamIdentifier.A)
        CC.GREEN else CC.RED
}

fun ItemBuilder.toRBTeamColor(teamSide: RBTeamSide) = color(teamSide.armorColor)
