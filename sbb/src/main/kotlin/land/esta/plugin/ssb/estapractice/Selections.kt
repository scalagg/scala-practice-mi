package land.esta.plugin.ssb.estapractice

import land.esta.plugin.ssb.bot.profile.RobotProfileService
import land.esta.plugin.ssb.bot.setting.BotSettingService
import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile
import org.bukkit.entity.Player
import java.util.UUID

/**
 * @author GrowlyX
 * @since 7/20/2024
 */
fun loadSettingsFromSelection(player: Player, selection: String, profileChoice: String?): SettingProfile?
{
    if (runCatching { UUID.fromString(selection) }.getOrNull() != null)
    {
        val profile = RobotProfileService.find(player)
            ?: return BotSettingService.loadDefaultProfile("EASY")

        return profile.profiles[profileChoice ?: "default"]
            ?: BotSettingService.loadDefaultProfile("EASY")
    }

    return BotSettingService.loadDefaultProfile(selection)
}
