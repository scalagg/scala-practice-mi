package gg.scala.practice.minigame.skywars.shared.cages

import com.cryptomorin.xseries.XMaterial
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.game.extensions.cosmetics.CosmeticRegistry
import gg.scala.game.extensions.cosmetics.ImmutableResource
import gg.scala.game.extensions.cosmetics.proxied.ProxyCosmeticCategory
import net.evilblock.cubed.util.bukkit.ItemBuilder

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
@Service
object CageCategory : ProxyCosmeticCategory<Cage>
{
    override val description = listOf(
        "Show off your skill by",
        "spawning in a cool cage!"
    )
    override val icon = ItemBuilder
        .of(XMaterial.PINK_STAINED_GLASS)

    override val name = "Cages"
    override val resource = ImmutableResource("cage")

    override fun proxied() = CageDataSync.cached().cages.values.toList()
    override fun convert(t: Cage) = CageCosmetic(t)

    @Configure
    fun configure()
    {
        CosmeticRegistry.registerCategory(this)
    }
}
