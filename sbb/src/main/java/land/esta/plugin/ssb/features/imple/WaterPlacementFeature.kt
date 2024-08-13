package land.esta.plugin.ssb.features.imple

import land.esta.plugin.ssb.bots.Bot
import land.esta.plugin.ssb.features.Feature
import me.lucko.helper.Schedulers
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @author TastyCake
 * @date 8/13/2024
 */
class WaterPlacementFeature : Feature()
{
    override fun tickEvent(bot: Bot)
    {
        if (System.currentTimeMillis() - 500 < bot.lastPlacedWater) return

        if (bot.healthManager.fireTicks < 5) return

        bot.lastPlacedWater = System.currentTimeMillis()

        val previousSlot = bot.inventoryManager.mainHand

        val buckets = bot.inventoryManager.hasItemUsingName(Material.WATER_BUCKET)

        if (buckets.size < 1) return

        val block = bot.movements.location.block

        bot.movements.isLockLocation = true
        bot.movements.rotations.rotationLockUntil = System.currentTimeMillis() + 1000L
        bot.movements.rotate(0f, 90f)

        Schedulers
            .sync()
            .runLater({
                bot.inventoryManager.mainHand = bot.inventoryManager.getItemSlot(buckets[0])
                block.type = Material.WATER
                bot.inventoryManager.setItem(bot.inventoryManager.mainHand, ItemStack(Material.BUCKET))

                Schedulers
                    .sync()
                    .runLater({
                        block.type = Material.AIR
                        bot.inventoryManager.setItem(bot.inventoryManager.mainHand, ItemStack(Material.WATER_BUCKET))
                        bot.inventoryManager.mainHand = previousSlot
                        bot.movements.isLockLocation = false
                    }, 10L)
                    .bindWith(bot.terminable)
            }, 10L)
            .bindWith(bot.terminable)
    }
}