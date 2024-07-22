package land.esta.plugin.ssb.bot.setting.setting

import land.esta.plugin.ssb.bots.setting.impl.AttackSettings
import land.esta.plugin.ssb.bots.setting.impl.ConnectionSettings
import land.esta.plugin.ssb.bots.setting.impl.EatingSettings
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
val root = Category(
    "root",
    listOf(),
    ItemStack(Material.AIR),
    null,
    "PvP Bot Settings",
    null
).apply {
    addChildren(
        AttackSettings(),
        ConnectionSettings(),
        EatingSettings()
    )
}

object RootAccessor
{
    val attackSettings = root.childByType<AttackSettings>("attacksettings")
    val eatingSettings = root.childByType<EatingSettings>("gapplesettings")
    val connectionSettings = root.childByType<ConnectionSettings>("connectionsettings")
}
