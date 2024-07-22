package land.esta.plugin.ssb.bot.setting.setting

import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
data class Option<T : Any>(
    override val id: String,
    override val description: List<String> = listOf(),
    override val item: ItemStack,
    override val parent: SettingLike?,
    override val displayName: String,
    val default: T,
) : SettingLike
{
    override fun describe(player: Player, context: SettingProfile) = super.describe(player, context) + listOf(
        "",
        "${CC.GRAY}Value: ${CC.AQUA}${
            context.value(this)
        }"
    )
}
