package gg.tropic.practice.resources

import gg.tropic.practice.games.GameService
import gg.tropic.practice.games.team.TeamIdentifier
import gg.tropic.practice.kit.feature.FeatureFlag
import gg.tropic.practice.utilities.RBTeamSide
import gg.tropic.practice.utilities.toRBTeamSide
import net.evilblock.cubed.nametag.NametagInfo
import net.evilblock.cubed.nametag.NametagProvider
import net.evilblock.cubed.nametag.NametagProviderRegister
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/9/2022
 */
@NametagProviderRegister
object DuelsNametagImpl : NametagProvider("practice", Int.MAX_VALUE)
{
    private val RED_TEAM_NAME_TAG = createNametag("${CC.B_RED}R${CC.RED} ", "")
    private val GREEN_TEAM_NAME_TAG = createNametag("${CC.B_GREEN}G${CC.GREEN} ", "")
    private val BLUE_TEAM_NAME_TAG = createNametag("${CC.B_BLUE}B${CC.BLUE} ", "")

    private val SPECTATOR_NAME_TAG = createNametag("${CC.B_GRAY}S${CC.GRAY} ", "")

    override fun fetchNametag(
        viewed: Player, viewer: Player
    ): NametagInfo?
    {
        val viewedGame = GameService
            .byPlayerOrSpectator(viewed.uniqueId)
            ?: return null

        val viewerGame = GameService
            .byPlayerOrSpectator(viewer.uniqueId)
            ?: return null

        if (viewerGame.expectation == viewedGame.expectation)
        {
            if (viewed.hasMetadata("spectator"))
            {
                return SPECTATOR_NAME_TAG
            }

            if (viewedGame.flag(FeatureFlag.RedBlueTeams))
            {
                val team = viewedGame.getNullableTeam(viewer)
                    ?: return SPECTATOR_NAME_TAG

                val rbSide = team
                    .teamIdentifier
                    .toRBTeamSide()

                return if (rbSide == RBTeamSide.Blue)
                    BLUE_TEAM_NAME_TAG else RED_TEAM_NAME_TAG
            }

            val team = viewedGame.getTeamOf(viewed)
            // TODO: proper nametags for each team
            return if (team.teamIdentifier == TeamIdentifier.A)
                GREEN_TEAM_NAME_TAG else RED_TEAM_NAME_TAG
        }

        return null
    }
}
