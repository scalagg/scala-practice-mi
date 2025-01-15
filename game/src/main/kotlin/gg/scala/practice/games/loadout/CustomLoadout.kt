package gg.scala.practice.games.loadout

import gg.scala.practice.kit.Kit
import gg.scala.practice.profile.loadout.Loadout
import gg.scala.practice.utilities.deepClone
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * @author GrowlyX
 * @since 9/25/2023
 */
class CustomLoadout(
    private val loadout: Loadout,
    private val kit: Kit
) : SelectedLoadout
{
    override fun displayName() = loadout.name
    override fun apply(player: Player)
    {
        player.inventory.clear()
        player.inventory.armorContents = kit.armorContents
        player.inventory.contents = loadout.inventoryContents.deepClone()

        if (player.activePotionEffects.isNotEmpty())
        {
            player.activePotionEffects
                .forEach { effect ->
                    player.removePotionEffect(effect.type)
                }
        }

        if (kit.potionEffects.isNotEmpty())
        {
            kit.potionEffects.forEach { (t, u) ->
                player.addPotionEffect(
                    PotionEffect(
                        PotionEffectType.getByName(t), Int.MAX_VALUE, u
                    )
                )
            }
        }

        player.fixInventoryBasedOnRBTeamColor()
        player.updateInventory()
    }
}
