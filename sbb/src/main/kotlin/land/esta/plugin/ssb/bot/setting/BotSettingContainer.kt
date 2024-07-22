package land.esta.plugin.ssb.bot.setting

import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile

data class BotSettingContainer(
    val map: MutableMap<String, SettingProfile> = mutableMapOf()
)
