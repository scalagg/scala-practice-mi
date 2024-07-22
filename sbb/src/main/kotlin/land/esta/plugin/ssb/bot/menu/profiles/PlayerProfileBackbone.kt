package land.esta.plugin.ssb.bot.menu.profiles

import land.esta.plugin.ssb.bot.profile.RobotProfileService
import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 7/22/2024
 */
class PlayerProfileBackbone(
    private val player: Player,
    private val settingProfile: SettingProfile
) : ProfileBackbone
{
    override fun save()
    {
        val profile = RobotProfileService.find(player)
            ?: return

        profile.save()
    }
}
