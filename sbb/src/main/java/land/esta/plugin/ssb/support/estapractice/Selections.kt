package land.esta.plugin.ssb.support.estapractice

import land.esta.plugin.ssb.bot.profile.BotSettingProfileService
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
        val profile = BotSettingProfileService.find(player)
            ?: return SettingManager.loadSettings("EASY")
        val setting = profile.settings["default"]
            ?: return SettingManager.loadSettings("EASY")

        return SettingManager.loadManagerFromSettings(profile, setting)
    }

    return SettingManager.loadSettings(selection)
}
