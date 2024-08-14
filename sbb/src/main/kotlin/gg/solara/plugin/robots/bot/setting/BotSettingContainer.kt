package gg.solara.plugin.robots.bot.setting

import gg.solara.plugin.robots.bot.setting.setting.persistence.SettingProfile

data class BotSettingContainer(
    val map: MutableMap<String, SettingProfile> = mutableMapOf()
)
