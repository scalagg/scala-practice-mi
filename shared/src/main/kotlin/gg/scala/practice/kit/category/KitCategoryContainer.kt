package gg.scala.practice.kit.category

import gg.scala.practice.kit.Kit

/**
 * @author Topu
 * @date 4/7/2025
 */
data class KitCategoryContainer(
    val categories: MutableList<KitCategory> = mutableListOf()
) {
    fun findById(id: String) = categories
        .firstOrNull {
            it.id.equals(id, true)
        }
}