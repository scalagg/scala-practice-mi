package gg.solara.practice.minigame.hctranked.shared.pvpclass.classes.event

import gg.solara.practice.minigame.hctranked.shared.pvpclass.PvPClass
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * @author Topu
 * @date 10/5/2024
 */
class BardRestoreEvent(
    private val player: Player?,
    private val potions: PvPClass.SavedPotion?
) : Event()
{

    override fun getHandlers(): HandlerList
    {
        return (Companion.handlers)
    }

    companion object
    {
        private val handlers = HandlerList()

        val handlerList: HandlerList
            get()
            = (handlers)
    }
}