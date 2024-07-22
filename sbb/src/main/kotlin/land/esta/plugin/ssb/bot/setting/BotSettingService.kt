package land.esta.plugin.ssb.bot.setting

import gg.scala.commons.persist.datasync.DataSyncKeys
import gg.scala.commons.persist.datasync.DataSyncService
import gg.scala.commons.persist.datasync.DataSyncSource
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service
import gg.tropic.practice.namespace
import gg.tropic.practice.namespaceShortened
import gg.tropic.practice.suffixWhenDev
import land.esta.plugin.ssb.SmartBotsBackend
import net.kyori.adventure.key.Key

@Service
object BotSettingService: DataSyncService<BotSettingContainer>()
{
    @Inject lateinit var plugin: SmartBotsBackend

    object BotSettingKeys : DataSyncKeys
    {
        override fun newStore() = "sbb-difficulty-presets"

        override fun store() = Key.key(namespace(), "bot-settings")
        override fun sync() = Key.key(namespaceShortened().suffixWhenDev(), "bsync")
    }

    override fun locatedIn() = DataSyncSource.Mongo

    override fun keys() = BotSettingKeys
    override fun type() = BotSettingContainer::class.java
}
