package gg.solara.practice.minigame.hctranked.shared.pvpclass.classes

import gg.solara.practice.minigame.hctranked.MiniGameHCTRanked
import gg.solara.practice.minigame.hctranked.shared.pvpclass.PvPClass
import gg.solara.practice.minigame.hctranked.shared.pvpclass.PvPClassService
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.time.TimeUtil
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap
import kotlin.collections.HashSet

/**
 * @author Topu
 * @date 10/2/2024
 */
class ArcherClass : PvPClass(
    "Archer",
    15,
    "LEATHER_",
    listOf(Material.SUGAR,
        Material.FEATHER
    ))
{

    companion object {
        const val MARK_SECONDS = 10

        private val lastSpeedUsage: MutableMap<UUID, Long> = HashMap()
        private val lastJumpUsage: MutableMap<UUID, Long> = HashMap()
        val markedPlayer: MutableMap<UUID, Long> = ConcurrentHashMap()

        private val markedBy: MutableMap<UUID, MutableSet<Pair<UUID, Long>>> = HashMap()

        @JvmStatic
        fun isMarked(player: Player): Boolean {
            return markedPlayer[player.uniqueId]?.let { it > System.currentTimeMillis() } ?: false
        }
    }

    override fun apply(player: Player) {
        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 2), true)
        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Int.MAX_VALUE, 0), true)
    }

    override fun tick(player: Player) {
        if (!qualifies(player.inventory)) {
            super.tick(player)
            return
        }

        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 2))
        }

        if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Int.MAX_VALUE, 0))
        }

        super.tick(player)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityArrowHit(event: EntityDamageByEntityEvent) {
        if (event.entity is Player && event.damager is Arrow) {
            val player = event.entity as Player
            val arrow = event.damager as Arrow

            val shooter = arrow.shooter as? Player ?: return
            val pullback = arrow.getMetadata("Pullback")[0].asFloat()

            if (!PvPClassService.hasKitOn(shooter, this)) return

            val match = PotPvP.getInstance().matchHandler.getMatchPlaying(shooter)

            match?.let {
                it.totalHitShots[shooter.uniqueId] = it.totalHitShots.getOrDefault(shooter.uniqueId, 0) + 1
            }

            val damage = if (isMarked(player)) 4 else 3

            if (pullback < 0.5F) {
                event.damage = 2.0
            } else {
                event.damage = 0.0
                player.health = maxOf(0.0, player.health - damage)
            }

            val shotFrom = arrow.getMetadata("ShotFromDistance")[0].value() as Location
            val distance = shotFrom.distance(player.location)

            if (PvPClassService.hasKitOn(player, this)) {
                player.sendMessage("${CC.YELLOW}[${CC.BLUE}Arrow Range ${CC.YELLOW}(${distance.toInt()}) ${CC.RED}Cannot mark other Archers. ${CC.YELLOW}(${damage / 2} heart${if (damage / 2 == 1) "" else "s"})${CC.YELLOW}]")
            } else if (pullback >= 0.5F) {
                player.sendMessage("${CC.YELLOW}[${CC.BLUE}Arrow Range ${CC.YELLOW}(${distance.toInt()}) ${CC.DARK_PURPLE}Marked player for ${MARK_SECONDS} seconds. ${CC.BLUE}(${damage / 2} heart${if (damage / 2 == 1) "" else "s"})${CC.YELLOW}]")

                if (!isMarked(player)) {
                    player.sendMessage("${CC.RED}${CC.BOLD}Marked! ${CC.WHITE}An archer has shot you and marked you (+25% damage) for ${MARK_SECONDS} seconds.")
                }

                markedPlayer[player.uniqueId] = System.currentTimeMillis() + MARK_SECONDS * 1000

                markedBy.computeIfAbsent(shooter.uniqueId) { HashSet() }
                markedBy[shooter.uniqueId]?.add(Pair(player.uniqueId, System.currentTimeMillis() + MARK_SECONDS * 1000))

                match?.let {
                    it.totalTags[shooter.uniqueId] = it.totalTags.getOrDefault(shooter.uniqueId, 0) + 1
                }
            } else {
                PotPvP.getInstance().messageService.sendMessage(shooter, "pvpclasses.archer.not-found", distance.toInt(), damage / 2)
            }
        }
    }

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            if (isMarked(player)) {
                val damager = when (event.damager) {
                    is Player -> event.damager as Player
                    is Projectile -> (event.damager as Projectile).shooter as? Player
                    else -> null
                }

                damager?.let {
                    if (!canUseMark(it, player)) return

                    event.damage *= 1.25
                }
            }
        }
    }

    @EventHandler
    fun onEntityShootBow(event: EntityShootBowEvent) {
        event.projectile.setMetadata("ShotFromDistance", FixedMetadataValue(MiniGameHCTRanked(), event.projectile.location))
        event.projectile.setMetadata("Pullback", FixedMetadataValue(MiniGameHCTRanked(), event.force))

        val shooter = event.entity as Player

        if (!PvPClassService.hasKitOn(shooter, this)) return

        val match = PotPvP.getInstance().matchHandler.getMatchPlaying(shooter)
        match?.let {
            it.totalShots[shooter.uniqueId] = it.totalShots.getOrDefault(shooter.uniqueId, 0) + 1
        }
    }

    override fun itemConsumed(player: Player, material: Material): Boolean {
        if (material == Material.SUGAR) {
            val lastUsage = lastSpeedUsage[player.uniqueId]
            if (lastUsage != null && lastUsage > System.currentTimeMillis()) {
                val millisLeft = lastUsage - System.currentTimeMillis()
                val msg = TimeUtil.formatIntoDetailedString((millisLeft / 1000).toInt())
                player.sendMessage("${CC.RED}You cannot use this for another ${msg}!")
                return false
            }

            lastSpeedUsage[player.uniqueId] = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30)
            player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 200, 3), true)
        } else {
            val lastUsage = lastJumpUsage[player.uniqueId]
            if (lastUsage != null && lastUsage > System.currentTimeMillis()) {
                val millisLeft = lastUsage - System.currentTimeMillis()
                val msg = TimeUtil.formatIntoDetailedString((millisLeft / 1000).toInt())
                player.sendMessage("${CC.RED}You cannot use this for another ${msg}!")
                return false
            }

            lastJumpUsage[player.uniqueId] = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1)
            player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 20 * 5, 4))
        }
        return true
    }

    private fun canUseMark(player: Player, victim: Player): Boolean {
        val match = PotPvP.getInstance().matchHandler.getMatchPlaying(player)
        match?.let {
            val team = it.getTeam(player.uniqueId)
            if (team != null) {
                val amount = team.allMembers.count { memberUUID ->
                    val member = Bukkit.getPlayer(memberUUID)
                    member != null && PvPClassService.hasKitOn(member, this)
                }

                if (amount > 3) {
                    player.sendMessage("${CC.RED}Your team has too many archers. Archer mark was not applied.")
                    return false
                }
            }
        }

        markedBy[player.uniqueId]?.forEach { pair ->
            if (pair.first == victim.uniqueId && pair.second > System.currentTimeMillis()) {
                return false
            }
        }
        return true
    }
}