package gg.solara.plugin.robots.bot.menu.profiles

import gg.solara.plugin.robots.bot.setting.setting.persistence.SettingProfile

/**
 * @author GrowlyX
 * @since 7/22/2024
 */
interface ProfileBackbone
{
    fun save(settings: SettingProfile)
}
