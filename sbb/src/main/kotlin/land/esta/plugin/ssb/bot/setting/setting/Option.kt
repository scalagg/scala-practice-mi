package land.esta.plugin.ssb.bot.setting.setting

import land.esta.plugin.ssb.bot.menu.profiles.ProfileBackbone
import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingProfile
import land.esta.plugin.ssb.bot.setting.setting.persistence.SettingValue
import land.esta.plugin.ssb.bot.setting.setting.persistence.toInternalValue
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 7/21/2024
 */
data class Option<T : Any>(
    override val id: String,
    override val description: List<String> = listOf(),
    override val item: ItemStack,
    override val parent: Category?,
    override val displayName: String,
    val default: T,
    val increment: Double = 1.0,
    override val permission: String? = null
) : SettingLike
{
    fun interact(
        player: Player,
        profile: SettingProfile,
        backbone: ProfileBackbone,
        clickType: ClickType
    )
    {
        val setting = profile.setting(this)

        fun updateRandomizerRanges()
        {
            if (setting.getObject() is Int)
            {
                val integer = setting.getObject() as Int
                profile.updateRandomizerRange(
                    this,
                    integer - setting.randomizer to integer + setting.randomizer
                )
            } else
            {
                val integer = setting.getObject() as Double
                profile.updateRandomizerRange(
                    this,
                    integer - setting.randomizer to integer + setting.randomizer
                )
            }
        }

        val newLines = when (true)
        {
            (setting.getObject() is Int), (setting.getObject() is Double) ->
            {/*
                listOf(
                    "",
                    "${CC.AQUA}Value: ${CC.WHITE}${context.value(this)}",
                    "${CC.GREEN}Randomizer: ${CC.WHITE}${setting.randomizer}",
                    "            ${CC.GRAY}(${CC.WHITE}${setting.randomizerMinMax.first} ${CC.GREEN}◀▶${CC.WHITE}${setting.randomizerMinMax.second}${CC.GRAY})",
                    "",
                    "${CC.GREEN}▲ ${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}${
                        "Left Click for Value"
                    }",
                    "${CC.GREEN}  ${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}${
                        "Shift Left Click for Randomizer"
                    }",
                    "${CC.RED}▼ ${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}${
                        "Right Click for Value"
                    }",
                    "${CC.RED}  ${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}${
                        "Shift Right Click for Randomizer"
                    }"
                )*/

                if (clickType == ClickType.SHIFT_LEFT)
                {
                    profile.updateRandomizer(
                        this,
                        setting.randomizer + setting.updateBy
                    )

                    updateRandomizerRanges()
                    backbone.save()
                    return
                }

                if (clickType == ClickType.LEFT)
                {
                    if (setting.getObject() is Int)
                    {
                        val integer = setting.getObject() as Int
                        setting.setObject(integer + increment.toInt())
                    } else
                    {
                        val integer = setting.getObject() as Double
                        setting.setObject(integer + increment)
                    }
                    backbone.save()
                    return
                }

                if (clickType == ClickType.RIGHT)
                {
                    if (setting.getObject() is Int)
                    {
                        val integer = setting.getObject() as Int
                        setting.setObject(integer - increment.toInt())
                    } else
                    {
                        val integer = setting.getObject() as Double
                        setting.setObject(integer - increment)
                    }
                    backbone.save()
                    return
                }

                if (clickType == ClickType.SHIFT_RIGHT)
                {
                    profile.updateRandomizer(
                        this,
                        setting.randomizer - setting.updateBy
                    )

                    updateRandomizerRanges()
                    backbone.save()
                    return
                } else
                {
                    /// ???? INTELLIJ?
                }
            }

            (setting.getObject() is Boolean) ->
            {
                val value = setting.getObject() as Boolean
                setting.setObject(!value)
                backbone.save()
            }

            else ->
            {

            }
        }
    }

    fun composeSettingValue(): SettingValue
    {
        return SettingValue(
            default.toInternalValue(),
            increment
        )
    }

    override fun describe(player: Player, context: SettingProfile): List<String>
    {
        val setting = context.setting(this)
        val existing = super.describe(player, context)
        val newLines = when (true)
        {
            (setting.getObject() is Int), (setting.getObject() is Double) ->
            {
                listOf(
                    "",
                    "${CC.AQUA}Value: ${CC.WHITE}${context.value(this)}",
                    "${CC.GREEN}Randomizer: ${CC.WHITE}${setting.randomizer}",
                    "            ${CC.GRAY}(${CC.WHITE}${setting.randomizerMinMax.first} ${CC.GREEN}◀▶${CC.WHITE}${setting.randomizerMinMax.second}${CC.GRAY})",
                    "",
                    "${CC.GREEN}▲ ${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}${
                        "Left Click for Value"
                    }",
                    "${CC.GREEN}  ${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}${
                        "Shift Left Click for Randomizer"
                    }",
                    "${CC.RED}▼ ${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}${
                        "Right Click for Value"
                    }",
                    "${CC.RED}  ${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}${
                        "Shift Right Click for Randomizer"
                    }"
                )
            }

            (setting.getObject() is Boolean) ->
            {
                val value = setting.getObject() as Boolean
                listOf(
                    "",
                    if (value) "&a&l│ &fOn" else "&c│ &fOn",
                    if (value) "&c│ &fOff" else "&a&l│ &fOff",
                    "",
                    "${CC.GREEN}Click to switch!"
                )
            }

            else -> listOf()
        }

        return existing + newLines
    }
}
