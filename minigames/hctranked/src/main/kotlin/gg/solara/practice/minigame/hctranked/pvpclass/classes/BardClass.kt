package gg.solara.practice.minigame.hctranked.shared.pvpclass.classes

import de.slikey.effectlib.util.MathUtils.random
import gg.scala.commons.annotations.Listeners
import gg.solara.practice.minigame.hctranked.MiniGameHCTRanked
import gg.solara.practice.minigame.hctranked.shared.pvpclass.PvPClass
import gg.solara.practice.minigame.hctranked.shared.pvpclass.PvPClassService
import gg.solara.practice.minigame.hctranked.shared.pvpclass.classes.effect.BardEffect
import gg.tropic.practice.games.team.GameTeam
import net.evilblock.cubed.util.CC
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Topu
 * @date 10/2/2024
 */
@Listeners
class BardClass : PvPClass(
    "Bard",
    15,
    "GOLD_",
    null
)
{

    private val bardClickEffects = mutableMapOf<Material, BardEffect>()
    private val bardPassiveEffects = mutableMapOf<Material, BardEffect>()

    val lastEffectUsage = ConcurrentHashMap<UUID, Long>()
    val energy = ConcurrentHashMap<UUID, Float>()
    private val debuffs: Set<PotionEffectType> = setOf(
        PotionEffectType.POISON,
        PotionEffectType.SLOW,
        PotionEffectType.WEAKNESS,
        PotionEffectType.HARM,
        PotionEffectType.WITHER
    )

    companion object {
        const val BARD_RANGE_HORIZONTAL = 25
        const val BARD_RANGE_VERTICAL = 20
        const val EFFECT_COOLDOWN = 10 * 1000
        const val MAX_ENERGY = 100f
        const val ENERGY_REGEN_PER_SECOND = 1f
    }

    init {
        // Click buffs
        bardClickEffects[Material.BLAZE_POWDER] = BardEffect.fromPotionAndEnergy(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1), 45)
        bardClickEffects[Material.SUGAR] = BardEffect.fromPotionAndEnergy(PotionEffect(PotionEffectType.SPEED, 20 * 6, 2), 20)
        bardClickEffects[Material.FEATHER] = BardEffect.fromPotionAndEnergy(PotionEffect(PotionEffectType.JUMP, 20 * 5, 6), 25)
        bardClickEffects[Material.IRON_INGOT] = BardEffect.fromPotionAndEnergy(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 2), 40)
        bardClickEffects[Material.GHAST_TEAR] = BardEffect.fromPotionAndEnergy(PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 2), 40)
        bardClickEffects[Material.MAGMA_CREAM] = BardEffect.fromPotionAndEnergy(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 45, 0), 40)
        // bardClickEffects[Material.FERMENTED_SPIDER_EYE] = BardEffect.fromEnergy(60)
        bardClickEffects[Material.WHEAT] = BardEffect.fromEnergy(25)

        // Click debuffs
        bardClickEffects[Material.SPIDER_EYE] = BardEffect.fromPotionAndEnergy(PotionEffect(PotionEffectType.WITHER, 20 * 5, 1), 35)

        // Passive buffs
        bardPassiveEffects[Material.BLAZE_POWDER] = BardEffect.fromPotion(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 6, 0))
        bardPassiveEffects[Material.SUGAR] = BardEffect.fromPotion(PotionEffect(PotionEffectType.SPEED, 20 * 6, 1))
        bardPassiveEffects[Material.FEATHER] = BardEffect.fromPotion(PotionEffect(PotionEffectType.JUMP, 20 * 6, 1))
        bardPassiveEffects[Material.IRON_INGOT] = BardEffect.fromPotion(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 6, 0))
        bardPassiveEffects[Material.GHAST_TEAR] = BardEffect.fromPotion(PotionEffect(PotionEffectType.REGENERATION, 20 * 6, 0))
        bardPassiveEffects[Material.MAGMA_CREAM] = BardEffect.fromPotion(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 6, 0))
        // bardPassiveEffects[Material.FERMENTED_SPIDER_EYE] = BardEffect.fromPotion(PotionEffect(PotionEffectType.INVISIBILITY, 20 * 6, 0))

        object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getServer().onlinePlayers) {
                    if (!PvPClassService.hasKitOn(player, this@BardClass)) {
                        continue
                    }

                    energy[player.uniqueId]?.let { currentEnergy ->
                        if (currentEnergy < MAX_ENERGY) {
                            energy[player.uniqueId] = minOf(MAX_ENERGY, currentEnergy + ENERGY_REGEN_PER_SECOND)
                        }
                    } ?: run {
                        energy[player.uniqueId] = 0f
                    }

                    val manaInt = energy[player.uniqueId]!!.toInt()
                    if (manaInt % 10 == 0) {
                        player.sendMessage("${CC.GOLD}Bard Energy${CC.GRAY}: ${CC.GREEN}${manaInt} ${CC.YELLOW}seconds!")
                    }
                }
            }
        }.runTaskTimer(MiniGameHCTRanked(), 15L, 20L)
    }

    override fun apply(player: Player) {
        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 1))
        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Int.MAX_VALUE, 1), true)
        player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, Int.MAX_VALUE, 0), true)
    }

    override fun tick(player: Player) {
        if (!qualifies(player.inventory)) {
            super.tick(player)
            return
        }

        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 1))
        }

        if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Int.MAX_VALUE, 1))
        }

        if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
            player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, Int.MAX_VALUE, 0))
        }

        player.itemInHand?.type?.let { itemType ->
            if (bardPassiveEffects.containsKey(itemType)) {
                // CUSTOM
                if (itemType == Material.FERMENTED_SPIDER_EYE && lastEffectUsage[player.uniqueId]?.let { it > System.currentTimeMillis() } == true) {
                    return
                }
                giveBardEffect(player, bardPassiveEffects[itemType]!!, true, false)
            }
        }
        super.tick(player)
    }

    override fun remove(player: Player) {
        energy.remove(player.uniqueId)

        for (bardEffect in bardClickEffects.values) {
            bardEffect.lastMessageSent.remove(player.name)
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (!event.action.name.contains("RIGHT_") || !event.hasItem() || !bardClickEffects.containsKey(event.item.type) ||
            !PvPClassService.hasKitOn(event.player, this) || !energy.containsKey(event.player.uniqueId)) {
            return
        }

        if (lastEffectUsage[event.player.uniqueId]?.let { it > System.currentTimeMillis() } == true && event.player.gameMode != GameMode.CREATIVE) {
            val millisLeft = lastEffectUsage[event.player.uniqueId]!! - System.currentTimeMillis()
            val value = millisLeft / 1000.0
            val sec = Math.round(10.0 * value) / 10.0

            event.player.sendMessage("${CC.RED}You cannot use this for another ${sec} seconds!")
            return
        }

        val bardEffect = bardClickEffects[event.item.type] ?: return

        if (bardEffect.energy > energy[event.player.uniqueId]!!) {
            event.player.sendMessage("${CC.RED}You do not have enough energy for this! You need ${bardEffect.energy} energy, but you only have ${energy[event.player.uniqueId]!!.toInt()}")
            return
        }

        energy[event.player.uniqueId] = energy[event.player.uniqueId]!! - bardEffect.energy

        val negative = bardEffect.potionEffect != null && debuffs.contains(bardEffect.potionEffect.type)

        lastEffectUsage[event.player.uniqueId] = System.currentTimeMillis() + EFFECT_COOLDOWN
        giveBardEffect(event.player, bardEffect, !negative, true)

        if (event.item.amount == 1) {
            event.player.itemInHand = ItemStack(Material.AIR)
            event.player.updateInventory()
        } else {
            event.item.amount -= 1
        }
    }

    fun giveBardEffect(source: Player, bardEffect: BardEffect, friendly: Boolean, persistOldValues: Boolean) {
        for (player in getNearbyPlayers(source, friendly)) {
            // Bards can't get Strength.
            if (PvPClassService.hasKitOn(player, this) && bardEffect.potionEffect != null && bardEffect.potionEffect.type == PotionEffectType.INCREASE_DAMAGE) {
                continue
            }

            bardEffect.potionEffect?.let {
                smartAddPotion(player, it, persistOldValues, this)
            } ?: run {
                val material = source.itemInHand.type
                giveCustomBardEffect(player, material)
            }
        }
    }

    fun giveCustomBardEffect(target: Player, material: Material) {
        when (material) {
            Material.BLAZE_POWDER -> target.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1))
            Material.SUGAR -> target.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20 * 5, 2))
            Material.FEATHER -> target.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 20 * 5, 6))
            Material.IRON_INGOT -> target.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 2))
            Material.GHAST_TEAR -> target.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 2))
            Material.MAGMA_CREAM -> target.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 45, 0))
            Material.WHEAT -> {
                val value = random.nextInt(6) // 0, 1, 2, 3, 4, 5
                target.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, value))
            }
            Material.SPIDER_EYE -> target.addPotionEffect(PotionEffect(PotionEffectType.WITHER, 20 * 5, 1))
            else -> throw IllegalArgumentException("Unknown material: $material")
        }

    }
}