package land.esta.plugin.ssb.bot.setting.setting

import land.esta.plugin.ssb.bots.setting.impl.AttackSettings
import land.esta.plugin.ssb.bots.setting.impl.ConnectionSettings
import land.esta.plugin.ssb.bots.setting.impl.EatingSettings
import land.esta.plugin.ssb.bots.setting.impl.FleeSettings
import land.esta.plugin.ssb.bots.setting.impl.PacketsSettings
import land.esta.plugin.ssb.bots.setting.impl.PearlSettings
import land.esta.plugin.ssb.bots.setting.impl.PlaystyleSettings
import land.esta.plugin.ssb.bots.setting.impl.PotionSettings
import land.esta.plugin.ssb.bots.setting.impl.RodSettings
import land.esta.plugin.ssb.bots.setting.impl.RotationSettings
import land.esta.plugin.ssb.bots.setting.impl.StrafeSettings
import land.esta.plugin.ssb.bots.setting.impl.VelocitySettings
import land.esta.plugin.ssb.bots.setting.impl.WTapSettings
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
        AttackSettings(this),
        ConnectionSettings(this),
        EatingSettings(this),
        FleeSettings(this),
        PacketsSettings(this),
        PearlSettings(this),
        PlaystyleSettings(this),
        PotionSettings(this),
        RodSettings(this),
        RotationSettings(this),
        StrafeSettings(this),
        VelocitySettings(this),
        WTapSettings(this)
    )
}

object RootAccessor
{
    val attackSettings = root.childByType<AttackSettings>("attacksettings")
    val eatingSettings = root.childByType<EatingSettings>("gapplesettings")
    val connectionSettings = root.childByType<ConnectionSettings>("connectionsettings")
    val fleeSettings = root.childByType<FleeSettings>("fleesettings")
    val packetSettings = root.childByType<PacketsSettings>("packetsettings")
    val pearlSettings = root.childByType<PearlSettings>("pearlsettings")
    val playStyleSettings = root.childByType<PlaystyleSettings>("playstylesettings")
    val potionSettings = root.childByType<PotionSettings>("potionsettings")
    val rodSettings = root.childByType<RodSettings>("rodsettings")
    val rotationSettings = root.childByType<RotationSettings>("rotationsettings")
    val strafeSettings = root.childByType<StrafeSettings>("strafesettings")
    val velocitySettings = root.childByType<VelocitySettings>("velocitysettings")
    val wTapSettings = root.childByType<WTapSettings>("wtapsettings")
}
