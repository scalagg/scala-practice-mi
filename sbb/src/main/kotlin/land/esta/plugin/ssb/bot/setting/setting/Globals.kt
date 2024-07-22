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
    val attackSettings = root.childByType<AttackSettings>("root.attacksettings")
    val eatingSettings = root.childByType<EatingSettings>("root.gapplesettings")
    val connectionSettings = root.childByType<ConnectionSettings>("root.connectionsettings")
    val fleeSettings = root.childByType<FleeSettings>("root.fleesettings")
    val packetSettings = root.childByType<PacketsSettings>("root.packetsettings")
    val pearlSettings = root.childByType<PearlSettings>("root.pearlsettings")
    val playStyleSettings = root.childByType<PlaystyleSettings>("root.playstylesettings")
    val potionSettings = root.childByType<PotionSettings>("root.potionsettings")
    val rodSettings = root.childByType<RodSettings>("root.rodsettings")
    val rotationSettings = root.childByType<RotationSettings>("root.rotationsettings")
    val strafeSettings = root.childByType<StrafeSettings>("root.strafesettings")
    val velocitySettings = root.childByType<VelocitySettings>("root.velocitysettings")
    val wTapSettings = root.childByType<WTapSettings>("root.wtapsettings")
}
