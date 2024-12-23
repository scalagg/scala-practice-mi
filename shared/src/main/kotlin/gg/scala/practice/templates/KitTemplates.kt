package gg.scala.practice.templates

import gg.scala.practice.kit.Kit
import gg.scala.practice.kit.feature.FeatureFlag

/**
 * @author GrowlyX
 * @since 7/17/2024
 */
object KitTemplates
{
    val templates = mutableMapOf<String, Kit>(
        "something" to Kit(
            id = "something",
            displayName = "",
            features = mutableMapOf(
                FeatureFlag.HeartsBelowNameTag to mutableMapOf()
            )
        )
    )
}
