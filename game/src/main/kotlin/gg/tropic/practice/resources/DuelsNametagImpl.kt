package gg.tropic.practice.resources

import gg.tropic.practice.games.GameService
import gg.tropic.practice.games.team.GameTeamSide
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
    private val GREEN_TEAM_NAME_TAG = createNametag("${CC.B_GREEN}G${CC.GREEN} ", "")
    private val RED_TEAM_NAME_TAG = createNametag("${CC.B_RED}R${CC.RED} ", "")
    private val BLUE_TEAM_NAME_TAG = createNametag("${CC.B_BLUE}B${CC.BLUE} ", "")

    private val SPECTATOR_NAME_TAG = createNametag("${CC.B_GRAY}S${CC.GRAY} ", "")

    override fun fetchNametag(
        toRefresh: Player, refreshFor: Player
    ): NametagInfo?
    {
        val playerGame = GameService
            .byPlayerOrSpectator(toRefresh.uniqueId)
            ?: return null

        val targetGame = GameService
            .byPlayerOrSpectator(refreshFor.uniqueId)
            ?: return null

        if (targetGame.expectation == playerGame.expectation)
        {
            if (refreshFor.hasMetadata("spectator"))
            {
                return SPECTATOR_NAME_TAG
            }

            if (playerGame.flag(FeatureFlag.RedBlueTeams))
            {
                val team = playerGame.getNullableTeam(refreshFor)
                    ?: return SPECTATOR_NAME_TAG

                val rbSide = team
                    .side
                    .toRBTeamSide()

                return if (rbSide == RBTeamSide.Blue)
                    BLUE_TEAM_NAME_TAG else RED_TEAM_NAME_TAG
            }

            return if (playerGame.getTeamOf(toRefresh).side == GameTeamSide.A)
                GREEN_TEAM_NAME_TAG else RED_TEAM_NAME_TAG
        }

        return null
    }
}
