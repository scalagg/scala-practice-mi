package gg.solara.practice.minigame.skywars.shared.cages

import gg.tropic.game.extensions.cosmetics.ImmutableResource
import gg.tropic.game.extensions.cosmetics.PurchasableCosmetic
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
class CageCosmetic(
    val cage: Cage,
    override val description: List<String> = cage.description,
    override val icon: ItemBuilder = ItemBuilder.copyOf(cage.item),
    override val name: String = cage.displayName,
    override val price: Float = cage.price
) : PurchasableCosmetic
{
    override val resource: ImmutableResource
        get() = ImmutableResource("cage", name)

    override fun equip(player: Player)
    {
    }

    override fun unequip(player: Player)
    {
    }
}
