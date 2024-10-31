package gg.solara.practice.minigame.hctranked.shared.pvpclass

import gg.scala.commons.annotations.Listeners
import gg.solara.practice.minigame.hctranked.MiniGameHCTRanked
import gg.solara.practice.minigame.hctranked.shared.pvpclass.classes.ArcherClass
import gg.solara.practice.minigame.hctranked.shared.pvpclass.classes.BardClass
import gg.solara.practice.minigame.hctranked.shared.pvpclass.classes.event.BardRestoreEvent
import net.evilblock.cubed.util.CC
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.player.*
import org.bukkit.potion.PotionEffect
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.function.Consumer

/**
 * @author Topu
 * @date 10/5/2024
 */
@Listeners
class PvPClassService : BukkitRunnable()
{

    companion object
    {
        private val equippedKits: MutableMap<String, PvPClass> = HashMap()
        private val savedPotions: MutableMap<UUID, PvPClass.SavedPotion> = HashMap()
        private val pvpClasses: MutableList<PvPClass> = mutableListOf(ArcherClass(), BardClass())

        fun getPvPClass(player: Player): PvPClass?
        {
            return equippedKits[player.name]
        }

        fun hasKitOn(player: Player, pvpClass: PvPClass): Boolean
        {
            return equippedKits[player.name] == pvpClass
        }
    }

    init
    {
        Bukkit.getServer().scheduler.runTaskTimer(MiniGameHCTRanked(), this, 2L, 2L)
    }

    fun shutdown()
    {
        equippedKits.keys.forEach { playerName ->
            equippedKits[playerName]?.remove(Bukkit.getServer().getPlayerExact(playerName))
        }
    }

    override fun run()
    {
        Bukkit.getServer().onlinePlayers.forEach { player ->
            if (equippedKits.containsKey(player.name))
            {
                val equippedPvPClass = equippedKits[player.name]!!
                if (!equippedPvPClass.qualifies(player.inventory))
                {
                    equippedKits.remove(player.name)
                    player.sendMessage("${CC.YELLOW}Class: ${CC.GOLD}${equippedPvPClass.name} ${CC.GRAY}--> ${CC.RED}Disabled${CC.YELLOW}!")

                    equippedPvPClass.remove(player)
                    PvPClass.removeInfiniteEffects(player)
                } else if (!player.hasMetadata("frozen"))
                {
                    equippedPvPClass.tick(player)
                }
            } else
            {
                val match = PotPvP.getInstance().matchHandler.getMatchPlayingOrSpectating(player) ?: return@forEach
                if (match.kitType != KitType.TEAM_FIGHT) return@forEach

                // Start kit warmup
                pvpClasses.forEach { pvpClass ->
                    if (pvpClass.qualifies(player.inventory) && pvpClass.canApply(player) && !player.hasMetadata("frozen"))
                    {
                        pvpClass.apply(player)
                        equippedKits[player.name] = pvpClass
                        player.sendMessage("${CC.YELLOW}Class: ${CC.GOLD}${pvpClass.name} ${CC.GRAY}--> ${CC.GREEN}Enabled${CC.YELLOW}!")
                        return@forEach
                    }
                }
            }
        }
        checkSavedPotions()
    }

    fun checkSavedPotions()
    {
        val idIterator: MutableIterator<Map.Entry<UUID, PvPClass.SavedPotion>> =
            savedPotions.entries.iterator()
        while (idIterator.hasNext())
        {
            val id: Map.Entry<UUID, PvPClass.SavedPotion> = idIterator.next()
            val player = Bukkit.getPlayer(id.key)
            if (player != null && player.isOnline)
            {
                Bukkit.getPluginManager().callEvent(BardRestoreEvent(player, id.value))
                if (id.value.time < System.currentTimeMillis() && !id.value.permanent)
                {
                    if (player.hasPotionEffect(id.value.potionEffect.type))
                    {
                        player.activePotionEffects.forEach(Consumer { potion: PotionEffect ->
                            val restore: PotionEffect = id.value.potionEffect
                            if (potion.type === restore.type && potion.duration < restore.duration && potion.amplifier <= restore.amplifier)
                            {
                                player.removePotionEffect(restore.type)
                            }
                        })
                    }

                    if (player.addPotionEffect(id.value.potionEffect, true))
                    {
                        Bukkit.getLogger().info(
                            ("${id.value.potionEffect.type}, ${id.value.potionEffect
                                .duration}, ${id.value.potionEffect.amplifier}")
                        )
                        idIterator.remove()
                    }
                }
            } else
            {
                idIterator.remove()
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerInteract(event: PlayerInteractEvent)
    {
        val player = event.player
        val itemInHand = player.itemInHand ?: return

        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return

        pvpClasses.forEach { pvPClass ->
            if (hasKitOn(player, pvPClass) && pvPClass.consumables?.contains(itemInHand.type) == true)
            {
                if (pvPClass.itemConsumed(player, event.item?.type ?: return))
                {
                    if (itemInHand.amount > 1)
                    {
                        itemInHand.amount -= 1
                    } else
                    {
                        player.inventory.remove(itemInHand)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent)
    {
        equippedKits.remove(event.player.name)?.remove(event.player)
    }

    @EventHandler
    fun onPlayerKick(event: PlayerKickEvent)
    {
        equippedKits.remove(event.player.name)?.remove(event.player)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent)
    {
        equippedKits.remove(event.player.name)?.remove(event.player)

        event.player.activePotionEffects.forEach { potionEffect ->
            if (potionEffect.duration > 1_000_000)
            {
                event.player.removePotionEffect(potionEffect.type)
            }
        }
    }

    @EventHandler
    fun onPlayerDamageEvent(event: PlayerItemDamageEvent)
    {
        val player = event.player
        val kit = equippedKits[player.name]
        if (kit?.name?.equals("bard", ignoreCase = true) == true)
        {
            if (player.inventory.armorContents.contains(event.item) && Random().nextBoolean())
            {
                event.isCancelled = true
            }
        }
    }
}