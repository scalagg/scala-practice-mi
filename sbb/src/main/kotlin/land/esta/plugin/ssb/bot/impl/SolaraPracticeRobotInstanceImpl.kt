package land.esta.plugin.ssb.bot.impl

import gg.tropic.practice.games.GameImpl
import gg.tropic.practice.games.robot.RobotInstance
import gg.tropic.practice.namespace
import land.esta.plugin.ssb.bot.setting.setting.RootAccessor
import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile
import land.esta.plugin.ssb.botevents.events.BotDeathEvent
import land.esta.plugin.ssb.bots.Bot
import land.esta.plugin.ssb.solara.SolaraPracticeIntegrator
import land.esta.plugin.ssb.solara.loadSettingsFromSelectionSync
import me.lucko.helper.Events
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.util.Color
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 7/20/2024
 */
class SolaraPracticeRobotInstanceImpl(private val gameImpl: GameImpl) : RobotInstance()
{
    private var spawnedBot: Bot? = null
    private val settingProfile: SettingProfile

    init
    {
        val settingsPlayer = gameImpl.humanSide().players.first()
        val selectedBot = ScalaCommonsSpigot.instance.kvConnection.sync()
            .get(
                "${namespace()}:bot-selection:$settingsPlayer"
            )
            ?: "EASY"

        val split = selectedBot.split(":")
        settingProfile = loadSettingsFromSelectionSync(
            settingsPlayer,
            split[0],
            split.getOrNull(1)
        ).deepCopy()

        terminable.with {
            if (spawnedBot?.isAlive == false)
            {
                return@with
            }

            // Auto destroy it if the terminable closes, we don't wanna waste resources
            spawnedBot?.silentKill()
        }

        Events
            .subscribe(BotDeathEvent::class.java)
            .filter { it.bot.uniqueId == spawnedBot?.uniqueId }
            .handler {
                gameImpl.complete(
                    gameImpl.humanSide()
                )
            }
            .bindWith(terminable)
    }

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
            )!! // we want to throw the error
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
