package land.esta.plugin.ssb.bot.menu.profiles

import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile

/**
 * @author GrowlyX
 * @since 7/22/2024
 */
interface ProfileBackbone
{
    fun save(settings: SettingProfile)
}
