package gg.solara.plugin.robots.solara

import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import gg.solara.plugin.robots.bot.profile.RobotProfileService
import gg.solara.plugin.robots.bot.profile.RobotSettingProfile
import gg.solara.plugin.robots.bot.profile.UnrankedBotsService
import gg.solara.plugin.robots.bot.setting.BotSettingService
import gg.solara.plugin.robots.bot.setting.setting.persistence.SettingProfile
import org.bukkit.Bukkit
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 7/20/2024
 */
fun loadSettingsFromSelectionSync(player: UUID, selection: String, profileChoice: String?): SettingProfile
{
    if (selection.equals("unranked", ignoreCase = true)) {
        val b = UnrankedBotsService.bots.values.random().settingProfile
        return b
    }

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
