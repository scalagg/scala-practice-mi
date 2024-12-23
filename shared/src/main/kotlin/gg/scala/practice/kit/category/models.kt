package gg.scala.practice.kit.category

import org.bukkit.Material

/**
 * @author GrowlyX
 * @since 10/12/2022
 */
data class KitCategoryConfigModel(
    val categories: MutableList<KitCategory> = mutableListOf()
)
{
    fun findById(id: String) = categories
        .firstOrNull {
            it.id.equals(id, true)
        }
}

data class KitCategory(
    val id: String,
    var name: String = id.capitalize(),
    var item: Material = Material.PAPER,
    var newCategory: Boolean = true,
    var description: MutableList<String> = mutableListOf()
)