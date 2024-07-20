package land.esta.plugin.ssb.bot.setting

import gg.scala.commons.persist.datasync.DataSyncKeys
import gg.scala.commons.persist.datasync.DataSyncService
import gg.scala.commons.persist.datasync.DataSyncSource
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.tropic.practice.namespace
import gg.tropic.practice.suffixWhenDev
import land.esta.plugin.ssb.SmartBotsBackend
import land.esta.plugin.ssb.utils.settings.DifficultiesSettings
import net.kyori.adventure.key.Key

@Service
object BotSettingService: DataSyncService<BotSettingContainer>()
{
    @Inject lateinit var plugin: SmartBotsBackend

    fun getOrCreate(identifier: String): BotSetting = runCatching { cached() }.getOrNull()?.map?.get(identifier) ?: BotSetting(identifier)

    @Configure
    fun configure()
    {
        plugin.difficultiesSettings = DifficultiesSettings()
    }

    object BotSettingKeys : DataSyncKeys
    {
        override fun newStore() = "mi-practice-bot-settings"

        override fun store() = Key.key(namespace(), "bot-settings")
        override fun sync() = Key.key(namespace().suffixWhenDev(), "bsync")
    }

    override fun locatedIn() = DataSyncSource.Mongo

    override fun keys() = BotSettingKeys
    override fun type() = BotSettingContainer::class.java
}