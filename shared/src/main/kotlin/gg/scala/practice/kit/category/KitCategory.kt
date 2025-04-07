package gg.scala.practice.kit.category

import gg.scala.practice.kit.KitService
import org.bukkit.Material

/**
 * @author Topu
 * @date 4/7/2025
 */
data class KitCategory(
    val id: String,
    val kits: MutableList<String> = mutableListOf(),
    var name: String = id.capitalize(),
    var item: Material = Material.PAPER,
    var newCategory: Boolean = true,
    var description: MutableList<String> = mutableListOf()
)
{

fun kits() = kits
    .mapNotNull {
        KitService.cached().kits[it]
    }
}