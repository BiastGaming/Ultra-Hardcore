package com.leontg77.ultrahardcore.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

/**
 * Anvil Repair event class.
 * 
 * @author LeonTG77
 */
public class AnvilRepairEvent extends Event implements Cancellable {
    private final Player player;

    private final AnvilInventory inv;
    private final ItemStack item;

    private final int cost;

    /**
     * AnvilRepair class constructor.
     *
     * @param player The player that renamed the item.
     * @param inv The inventory being used.
     * @param item The item renamed.
     * @param cost The repair cost.
     */
    public AnvilRepairEvent(Player player, AnvilInventory inv, ItemStack item, int cost) {
        this.player = player;

        this.item = item;
        this.inv = inv;

        this.cost = cost;
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
     * Get the level cost of the repair.
     * 
     * @return The cost.
     */
    public int getRepairCost() {
        return cost;
    }
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    public HandlerList getHandlers() {
        return HANDLERS;
    }
     
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}