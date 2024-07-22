package land.esta.plugin.ssb.bot.setting.setting

import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
interface SettingLike : AbstractTypeSerializable
{
    val id: String
    val displayName: String

    val description: List<String>
    val item: ItemStack
    val parent: SettingLike?

    fun toItemStack(player: Player, context: SettingProfile) = ItemBuilder
        .copyOf(item)
        .name("${CC.GREEN}$displayName")
        .setLore(describe(player, context))
        .build()

    fun describe(player: Player, context: SettingProfile): List<String>
    {
        return description.map { "${CC.WHITE}$it" }
    }

    fun composeId(): String
    {
        if (parent == null)
        {
            return id
        }

        return "${parent!!.composeId()}.$id"
    }
}
