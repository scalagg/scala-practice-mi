package land.esta.plugin.ssb.bot.profile

import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import land.esta.plugin.ssb.bot.setting.BotSetting
import land.esta.plugin.ssb.bots.setting.SettingManager
import java.util.*


data class BotSettingProfile(
    override val identifier: UUID
) : IDataStoreObject
{
    var skin: String? = null
    var name: String? = null
    val settings: MutableMap<String, BotSetting> = mutableMapOf()

    init
    {
        // Give them the default botsetting and use their identifier as the setting id
        settings["default"] = BotSetting(identifier.toString())
    }

    fun save() = DataStoreObjectControllerCache
        .findNotNull<BotSettingProfile>()
        .save(this)

}