package org.bukkit.event.block.chest;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author GrowlyX
 * @since 8/26/2024
 */
public class EntityChestCloseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private ChestCloseSoundAction chestCloseSoundSetting = ChestCloseSoundAction.BROADCAST;
    private boolean shouldSendCloseAnimation = true;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public ChestCloseSoundAction getChestCloseSoundSetting() {
        return this.chestCloseSoundSetting;
    }

    public boolean isShouldSendCloseAnimation() {
        return this.shouldSendCloseAnimation;
    }

    public void setChestCloseSoundSetting(ChestCloseSoundAction chestCloseSoundSetting) {
        this.chestCloseSoundSetting = chestCloseSoundSetting;
    }

    public void setShouldSendCloseAnimation(boolean shouldSendCloseAnimation) {
        this.shouldSendCloseAnimation = shouldSendCloseAnimation;
    }
}
