package land.esta.plugin.ssb.bot.setting.setting

import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
data class Category(
    override val id: String,
    override val description: List<String>,
    override val item: ItemStack,
    override val parent: SettingLike?,
    override val displayName: String
) : SettingLike
{
    internal val children = mutableMapOf<String, SettingLike>()
    fun addChildren(vararg settingLike: SettingLike)
    {
        settingLike.forEach {
            children[it.composeId()] = it
        }
    }

    override fun describe(player: Player, context: SettingProfile) = super.describe(player, context) + listOf(
        "",
        "${CC.GREEN}Click to view!"
    )

    override fun getAbstractType() = Category::class.java
}
