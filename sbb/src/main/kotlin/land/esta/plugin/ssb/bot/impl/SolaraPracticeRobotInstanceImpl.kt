package land.esta.plugin.ssb.bot.impl

import gg.tropic.practice.games.GameImpl
import gg.tropic.practice.games.robot.RobotInstance
import land.esta.plugin.ssb.bot.setting.setting.RootAccessor
import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile
import land.esta.plugin.ssb.bots.Bot
import land.esta.plugin.ssb.solara.SolaraPracticeIntegrator
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Color
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 7/20/2024
 */
class SolaraPracticeRobotInstanceImpl(private val gameImpl: GameImpl, settingProfileOriginal: SettingProfile, instanceID: Int) : RobotInstance()
{
    private var spawnedBot: Bot? = null
    private val settingProfile = settingProfileOriginal.deepCopy()

    init
    {
        if (instanceID > 1)
        {
            settingProfile.botName = "${CC.YELLOW}Minion #$instanceID"
        }

        terminable.with {
            if (spawnedBot?.isAlive == false)
            {
                return@with
            }

            // Auto destroy it if the terminable closes, we don't wanna waste resources
            spawnedBot?.silentKill()
        }
    }

    override fun solaraID() = spawnedBot?.solaraID ?: throw IllegalStateException("Robot has not spawned in yet")
    override fun name(): String = spawnedBot?.profile?.botName?.let(Color::translate) ?: ""
    override fun ping() = spawnedBot?.profile?.value(RootAccessor.connectionSettings.ping) ?: 100

    override fun initialSpawn()
    {
        if (spawnedBot == null)
        {
            spawnedBot = SolaraPracticeIntegrator.spawn(
                gameImpl.map
                    .findSpawnLocationMatchingTeam(
                        gameImpl.robotSide().side
                    )!!
                    .toLocation(gameImpl.arenaWorld!!),
                gameImpl,
                settingProfile
            )
        }
    }

    override fun participantConnected(player: Player)
    {
        spawnedBot?.viewsManager?.addPlayer(player)
    }

    override fun spectatorConnected(player: Player)
    {
        spawnedBot?.viewsManager?.addPlayer(player)
    }

    override fun destroy()
    {
        spawnedBot?.silentKill()
    }
}
