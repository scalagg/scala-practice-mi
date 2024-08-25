package gg.solara.minigame.skywars

import gg.solara.minigame.skywars.shared.SkyWarsGameConfiguration
import gg.tropic.practice.games.GameImpl
import gg.tropic.practice.games.GameReport
import gg.tropic.practice.minigame.MiniGameLifecycle
import gg.tropic.practice.minigame.MiniGameScoreboard
import me.lucko.helper.terminable.composite.CompositeTerminable
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
class SkyWarsLifecycle(
    override val configuration: SkyWarsGameConfiguration,
    override val game: GameImpl
) : MiniGameLifecycle<SkyWarsGameConfiguration>, CompositeTerminable by CompositeTerminable.create()
{
    override val scoreboard: MiniGameScoreboard = SkyWarsScoreboard(game)
    override fun configure()
    {

    }

    inner class SkyWarsScoreboard(override val game: GameImpl) : MiniGameScoreboard
    {
        override fun createWaitingScoreboardFor(player: Player, board: List<String>)
        {

        }

        override fun createInGameScoreboardFor(player: Player, board: List<String>)
        {

        }

        override fun createEndingScoreboardFor(player: Player, report: GameReport?, board: List<String>)
        {

        }
    }
}
