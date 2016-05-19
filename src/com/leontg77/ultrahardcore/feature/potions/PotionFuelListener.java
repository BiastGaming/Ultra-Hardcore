package com.leontg77.ultrahardcore.feature.potions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Optional;

/**
 * Potion Fuel listener class.
 * 
 * @author ghowden
 * @see licenses/UHC
 * @see https://github.com/Eluinhost/UHC/blob/1.1.0/src/main/java/gg/uhc/uhc/modules/potions/PotionFuelsListener.java
 */
public class PotionFuelListener implements Listener {
    private final Map<Material, String> messages = new HashMap<Material, String>();
    private Set<Material> disabled = messages.keySet();

    /**
     * Block the given material to be brewed.
     *
     * @param material The material to block.
     * @param message The message to send when someone tries to use it.
     */
    protected void addMaterial(Material material, String message) {
        messages.put(material, message);
        disabled = messages.keySet();
    }

    /**
     * Unblock the given material to be brewed.
     *
     * @param material The material to unblock.
     */
    protected void removeMaterial(Material material) {
        disabled.remove(material);
    }

    // cancel hoppers moving the item into the stand
    @EventHandler(ignoreCancelled = true)
    public void on(InventoryMoveItemEvent event) {
        Inventory dest = event.getDestination();

        if (dest.getType() != InventoryType.BREWING) {
            return;
        }

        ItemStack item = event.getItem();

        if (disabled.contains(item.getType())) {
            event.setCancelled(true);
        }
    }

    // stop dragging over the fuel slot
    @EventHandler(ignoreCancelled = true)
    public void on(InventoryDragEvent event) {
        Inventory inv = event.getInventory();

        if (inv.getType() != InventoryType.BREWING) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getOldCursor();

        // if it's not a disabled type do nothing
        if (!disabled.contains(item.getType())) {
            return;
        }

        // check if they dragged over the fuel
        // 3 is the fuel slot
        if (event.getRawSlots().contains(3)) {
            player.sendMessage(messages.get(item.getType()));
            event.setCancelled(true);
        }
    }

    // cancel click events going into the stand
    @EventHandler(ignoreCancelled = true)
    public void on(InventoryClickEvent event) {
        Inventory clickedInv = event.getClickedInventory();
        Inventory inv = event.getInventory();

        if (inv.getType() != InventoryType.BREWING)
            return;

        // quick exit
        if (disabled.size() == 0) {
            return;
        }

        // clicked outside of the window
        if (clickedInv == null) {
            return;
        }

        InventoryType clicked = clickedInv.getType();

        // get any relevant stack to check the type of based on the action took
        Optional<ItemStack> relevant = Optional.absent();

        switch (event.getAction()) {
        case MOVE_TO_OTHER_INVENTORY:
            // only worry about player -> stand
            if (clicked == InventoryType.PLAYER) {
                relevant = Optional.fromNullable(event.getCurrentItem());
            }
            break;
        case PLACE_ALL:
        case PLACE_SOME:
        case PLACE_ONE:
        case SWAP_WITH_CURSOR:
            // only worry about within a stand
            if (clicked == InventoryType.BREWING) {
                relevant = Optional.fromNullable(event.getCursor());
            }
            break;
        case HOTBAR_SWAP:
            // only worry about within a stand
            if (clicked == InventoryType.BREWING) {
                relevant = Optional.fromNullable(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()));
            }
            break;
        default:
            break;
        }

        if (relevant.isPresent() && disabled.contains(relevant.get().getType())) {
            event.getWhoClicked().sendMessage(messages.get(relevant.get().getType()));
            event.setCancelled(true);
        }
    }
}