package gg.tropic.practice.player

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 10/13/2023
 */
val LobbyPlayer.player: Player
    get() = Bukkit.getPlayer(uniqueId)

val String.component: Component
    get() = LegacyComponentSerializer
        .legacySection()
        .deserialize(this)


fun Player.resetAttributes()
{
    this.health = this.maxHealth

    this.foodLevel = 20
    this.saturation = 12.8f
    this.maximumNoDamageTicks = 20
    this.fireTicks = 0
    this.fallDistance = 0.0f
    this.level = 0
    this.exp = 0.0f
    this.walkSpeed = 0.2f
    this.inventory.heldItemSlot = 0

    this.inventory.clear()
    this.inventory.armorContents = null

    this.closeInventory()

    this.gameMode = GameMode.SURVIVAL
    this.fireTicks = 0

    for (potionEffect in this.activePotionEffects)
    {
        this.removePotionEffect(potionEffect.type)
    }

    this.updateInventory()
}
