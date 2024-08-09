package land.esta.plugin.ssb.solara

import land.esta.plugin.ssb.bot.profile.RobotProfileService
import land.esta.plugin.ssb.bot.setting.BotSettingService
import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile
import java.util.UUID

/**
 * @author GrowlyX
 * @since 7/20/2024
 */
fun loadSettingsFromSelectionSync(player: UUID, selection: String, profileChoice: String?): SettingProfile
{
    if (runCatching { UUID.fromString(selection) }.getOrNull() != null)
    {
        val profile = RobotProfileService.find(player)
            ?: RobotProfileService.loadCopyOf(player).join()
            ?: return BotSettingService.loadDefaultProfile("EASY")

        return profile.profiles[profileChoice ?: "default"]
            ?: BotSettingService.loadDefaultProfile("EASY")
    }

    return BotSettingService.loadDefaultProfile(selection)
}
