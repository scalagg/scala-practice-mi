package gg.scala.practice.listener

import gg.scala.commons.annotations.Listeners
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.ExplosionPrimeEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.weather.WeatherChangeEvent

/**
 * @author Topu
 * @date 1/15/2025
 */
@Listeners
class WorldListener : Listener
{
    @EventHandler
    fun onWeatherChange(event: WeatherChangeEvent)
    {
        if (event.toWeatherState()) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onDamage(event: EntityDamageEvent)
    {
        if (event.entity !is Player) return
        if (event.cause in setOf(
                EntityDamageEvent.DamageCause.FALL,
                EntityDamageEvent.DamageCause.MAGIC,
                EntityDamageEvent.DamageCause.FIRE_TICK,
                EntityDamageEvent.DamageCause.POISON
            )
        )
        {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onLeavesDecay(event: LeavesDecayEvent)
    {
        event.isCancelled = true
    }

    @EventHandler
    fun onHangingBreak(event: HangingBreakEvent)
    {
        event.isCancelled = true
    }

    @EventHandler
    fun onBlockBurn(event: BlockBurnEvent)
    {
        event.isCancelled = true
    }

    @EventHandler
    fun onBlockSpread(event: BlockSpreadEvent)
    {
        event.isCancelled = true
    }

    @EventHandler
    fun onBlockGrow(event: BlockGrowEvent)
    {
        event.isCancelled = true
    }

    @EventHandler
    fun onPhysics(event: BlockPhysicsEvent)
    {
        if (!event.block.isLiquid) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockForm(event: BlockFormEvent)
    {
        event.isCancelled = true
    }

    @EventHandler
    fun onBlockFade(event: BlockFadeEvent)
    {
        if (!event.block.isLiquid) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onCreatureSpawn(event: CreatureSpawnEvent)
    {
        // Cancel all monster and animal spawns
        event.isCancelled = true
    }

    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent)
    {
        event.isCancelled = true
    }

    @EventHandler
    fun onExplosionPrime(event: ExplosionPrimeEvent)
    {
        event.isCancelled = true
    }

    @EventHandler
    fun onBlockExplode(event: BlockExplodeEvent)
    {
        event.isCancelled = true
    }
}