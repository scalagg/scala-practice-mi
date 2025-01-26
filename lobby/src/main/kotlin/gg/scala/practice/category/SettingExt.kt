package gg.scala.practice.category

import gg.scala.basics.plugin.profile.BasicsProfileService
import gg.scala.practice.settings.restriction.RangeRestriction
import gg.scala.practice.settings.PracticeSettingCategory
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 10/15/2023
 */
val Player.pingRange: RangeRestriction
    get() = BasicsProfileService.find(this)
        ?.setting<RangeRestriction>(
            "${PracticeSettingCategory.PRACTICE_SETTING_PREFIX}:restriction-ping",
            RangeRestriction.None
        )
        ?: RangeRestriction.None
