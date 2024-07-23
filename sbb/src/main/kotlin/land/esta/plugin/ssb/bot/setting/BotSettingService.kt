package land.esta.plugin.ssb.bot.setting

import gg.scala.commons.persist.datasync.DataSyncKeys
import gg.scala.commons.persist.datasync.DataSyncService
import gg.scala.commons.persist.datasync.DataSyncSource
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service
import gg.tropic.practice.namespace
import gg.tropic.practice.namespaceShortened
import gg.tropic.practice.suffixWhenDev
import land.esta.plugin.ssb.EstaRobotsPlugin
import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile
import net.kyori.adventure.key.Key

@Service
object BotSettingService: DataSyncService<BotSettingContainer>()
{
    @Inject lateinit var plugin: EstaRobotsPlugin

    object BotSettingKeys : DataSyncKeys
    {
        override fun newStore() = "sbb-difficulty-presets"

        override fun store() = Key.key(namespace(), "bot-settings")
        override fun sync() = Key.key(namespaceShortened().suffixWhenDev(), "bsync")
    }

    fun loadDefaultProfile(difficulty: String): SettingProfile
    {
        return cached().map[difficulty] ?: SettingProfile(difficulty)
    }

    override fun postReload()
    {
        if (cached().map.isEmpty())
        {
            val cached = cached()
            listOf("EASY", "NORMAL", "HARD", "EXPERT", "NIGHTMARE").forEach {
                cached.map.putIfAbsent(it, SettingProfile(it))
            }
            sync(cached)
        }
    }

    override fun locatedIn() = DataSyncSource.Mongo

    override fun keys() = BotSettingKeys
    override fun type() = BotSettingContainer::class.java
}
