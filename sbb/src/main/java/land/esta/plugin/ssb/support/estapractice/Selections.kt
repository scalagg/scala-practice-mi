package land.esta.plugin.ssb.support.estapractice

import land.esta.plugin.ssb.bot.profile.RobotProfileService
import land.esta.plugin.ssb.bots.setting.SettingManager
import org.bukkit.entity.Player
import java.util.UUID

/**
 * @author GrowlyX
 * @since 7/20/2024
 */
fun loadSettingsFromSelection(player: Player, selection: String): SettingManager?
{
    if (runCatching { UUID.fromString(selection) }.getOrNull() != null)
    {
        val profile = RobotProfileService.find(player)
            ?: return SettingManager.loadSettings("EASY")

        return SettingManager.loadManagerFromSettings(profile)
    }

    return SettingManager.loadSettings(selection)
}
