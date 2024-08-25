package gg.solara.practice.minigame.skywars.shared.cages

import gg.tropic.game.extensions.cosmetics.CosmeticLike
import gg.tropic.game.extensions.cosmetics.ImmutableResource
import gg.tropic.game.extensions.cosmetics.PurchasableCosmetic
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/25/2024
 */
class CageNonPurchasableCosmetic(
    cage: Cage,
    override val description: List<String> = cage.description,
    override val icon: ItemBuilder = ItemBuilder.copyOf(cage.item),
    override val name: String = cage.displayName
) : CosmeticLike
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
