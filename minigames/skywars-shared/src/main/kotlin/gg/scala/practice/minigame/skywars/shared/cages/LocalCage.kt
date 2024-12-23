package gg.scala.practice.minigame.skywars.shared.cages

import org.bukkit.util.Vector
import java.util.*

data class LocalCage(
    val cage: SchemaCage,
    val location: Vector,
    val owner: UUID
)
