package com.leontg77.ultrahardcore.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.leontg77.ultrahardcore.listeners.QuitMessageListener.LogoutReason;

/**
 * Called when the player logs out.
 */
public class PlayerLeaveEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    
    private LogoutReason reason;
    private String quitMessage;

    public PlayerLeaveEvent(Player who, String quitMessage, LogoutReason reason) {
        super(who);

        this.quitMessage = quitMessage;
        this.reason = reason;
    }

    /**
     * Gets the quit message to send to all online players
     *
     * @return string quit message
     */
    public String getQuitMessage() {
        return quitMessage;
    }

    /**
     * Sets the quit message to send to all online players
     *
     * @param quitMessage quit message
     */
    public void setQuitMessage(String quitMessage) {
        this.quitMessage = quitMessage;
    }

    /**
     * Get the reason they logged out for.
     * 
     * @return The logout reason.
     */
    public LogoutReason getLogoutReason() {
        return reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
