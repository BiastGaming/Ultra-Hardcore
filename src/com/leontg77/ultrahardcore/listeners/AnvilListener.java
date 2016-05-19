package com.leontg77.ultrahardcore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.AnvilRenameEvent;
import com.leontg77.ultrahardcore.events.AnvilRepairEvent;

/**
 * Anvil listener class.
 * 
 * @author LeonTG77
 */
public class AnvilListener implements Listener {
    private final Main plugin;

    /**
     * Anvil listener class consturctor.
     *
     * @param plugin The main class.
     */
    public AnvilListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRepair(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked(); // safe cast.
        final Inventory inv = event.getClickedInventory();

        if (!(inv instanceof AnvilInventory)) {
            return;
        }

        final AnvilInventory anvil = (AnvilInventory) inv;
        int slot = event.getRawSlot();

        if (slot != 2) {
            return;
        }

        final ItemStack item = event.getCurrentItem();

        if (item == null) {
            return;
        }

        final int before = player.getLevel();

        new BukkitRunnable() {
            public void run() {
                int taken = before - player.getLevel();

                if (player.getLevel() >= taken) {
                    AnvilRepairEvent repair = new AnvilRepairEvent(player, anvil, item, taken);
                    Bukkit.getPluginManager().callEvent(repair);

                    event.setCancelled(repair.isCancelled());
                }
            }
        }.runTaskLater(plugin, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRename(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked(); // safe cast.
        Inventory inv = event.getClickedInventory();

        if (!(inv instanceof AnvilInventory)) {
            return;
        }

        AnvilInventory anvil = (AnvilInventory) inv;
        int slot = event.getRawSlot();

        if (slot != 2) {
            return;
        }

        ItemStack item = event.getCurrentItem();
        ItemStack old = anvil.getItem(0);

        if (item == null || old == null) {
            return;
        }

        if (old.hasItemMeta() && old.getItemMeta().hasDisplayName() && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            String newName = item.getItemMeta().getDisplayName();
            String oldName = old.getItemMeta().getDisplayName();

            if (oldName.equals(newName)) {
                return;
            }

            AnvilRenameEvent rename = new AnvilRenameEvent(player, anvil, item, oldName, newName);
            Bukkit.getPluginManager().callEvent(rename);

            event.setCancelled(rename.isCancelled());
        }
    }
}