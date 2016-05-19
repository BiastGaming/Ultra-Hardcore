package com.leontg77.ultrahardcore.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

/**
 * Anvil Rename event class.
 * 
 * @author LeonTG77
 */
public class AnvilRenameEvent extends Event implements Cancellable {
    private final Player player;

    private final AnvilInventory inv;
    private final ItemStack item;

    private final String oldName;
    private final String newName;

    /**
     * AnvilRename class constructor.
     *
     * @param player The player that renamed the item.
     * @param inv The inventory being used.
     * @param item The item renamed.
     * @param oldName The old name.
     * @param newName The new name.
     */
    public AnvilRenameEvent(Player player, AnvilInventory inv, ItemStack item, String oldName, String newName) {
        this.player = player;

        this.item = item;
        this.inv = inv;

        this.oldName = oldName;
        this.newName = newName;
    }

    private boolean isCancelled = false;

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
    
    @Override
    public boolean isCancelled() {
        // TODO Auto-generated method stub
        return isCancelled;
    }

    /**
     * Get the player that renamed the item.
     * 
     * @return The player.
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Get the inventory being used for this event.
     * 
     * @return The inventory.
     */
    public AnvilInventory getInventory() {
        return inv;
    }

    /**
     * Get the item that got renamed.
     * 
     * @return The item.
     */
    public ItemStack getItem() {
        return item;
    }
    
    /**
     * Get the old name of the item.
     * 
     * @return The old name.
     */
    public String getOldName() {
        return oldName;
    }
    
    /**
     * Get the new name of the item.
     * 
     * @return The new name.
     */
    public String getNewName() {
        return newName;
    }
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    public HandlerList getHandlers() {
        return HANDLERS;
    }
     
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}