package gg.scala.practice.cooldown

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.cooldown.CooldownHandler
import gg.scala.lemon.cooldown.CooldownHandler.notifyAndContinueNoBypass
import gg.scala.lemon.cooldown.type.PlayerStaticCooldown
import gg.scala.practice.PracticeGame
import gg.scala.practice.games.GameService
import gg.scala.practice.kit.feature.FeatureFlag
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import net.evilblock.cubed.util.CC
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * @author Topu
 * @date 1/15/2025
 */
@Service
object ArrowCooldown : PlayerStaticCooldown(
    "Arrow", 0L
) {
    @Inject
    lateinit var plugin: PracticeGame

    override fun durationFor(player: Player): Long {
        val game = GameService.byPlayer(player) ?: return 0L

        val duration = game.kit
            .featureConfig(
                FeatureFlag.ArrowCooldown,
                "duration"
            )

        return TimeUnit.SECONDS.toMillis(duration.toLong())
    }

    @Configure
    fun configure() {
        this.notifyOnExpiration()
        this.whenExpired {
            it.sendMessage("${CC.GRAY}Your arrow cooldown has expired.")
            giveArrow(it)
        }
        this.scheduleLevelChangeAfterCooldownChange()
        this.blockUnwantedArrows()
        this.launchArrowCooldown()
    }

    private fun scheduleLevelChangeAfterCooldownChange() {
        Schedulers.async().runRepeating(Runnable {
            for (id in tasks.keys) {
                val player = Bukkit.getPlayer(id) ?: continue
                val time = fetchRemaining(player)
                if (time < 0) continue

                val game = GameService.byPlayer(player) ?: continue
                val duration = game.kit.featureConfig(
                    FeatureFlag.ArrowCooldown,
                    "duration"
                ).toFloat()

                val seconds = (time.toDouble() / 1000.0).roundToInt()

                player.level = max(seconds, 0)
                player.exp = (time.toFloat() / (duration * 1000.0f)).coerceIn(0.0f, 1.0f)
            }
        }, 0L, 1L)
    }

    private fun blockUnwantedArrows() {
        Events.subscribe(PlayerInteractEvent::class.java)
            .filter {
                it.hasItem() && it.action.name.contains("RIGHT") && it.item.type == Material.BOW
            }
            .handler {
                val player = it.player
                val game = GameService.byPlayer(player) ?: return@handler

                if (!game.ensurePlaying() || player.hasMetadata("light-spectator")) {
                    it.isCancelled = true
                    player.updateInventory()
                    player.sendMessage("${CC.RED}You cannot shoot arrows right now!")
                    return@handler
                }

                if (!notifyAndContinueNoBypass(this.javaClass, player, "shooting an arrow")) {
                    it.isCancelled = true
                    player.updateInventory()
                }
            }
            .bindWith(plugin)
    }

    private fun launchArrowCooldown() {
        Events.subscribe(EntityShootBowEvent::class.java)
            .filter { it.entity is Player }
            .handler {
                val player = it.entity as Player
                if (!it.isCancelled) {
                    val game = GameService.byPlayer(player) ?: return@handler
                    if (game.flag(FeatureFlag.ArrowCooldown)) {
                        addOrOverride(player)
                    }
                }
            }
            .bindWith(plugin)

        CooldownHandler.register(this)
    }

    private fun giveArrow(player: Player) {
        val inventory = player.inventory
        val arrowStack = ItemStack(Material.ARROW, 1)

        val remaining = inventory.addItem(arrowStack)
        if (remaining.isNotEmpty()) {
            player.world.dropItem(player.location, arrowStack)
        }
    }
}
