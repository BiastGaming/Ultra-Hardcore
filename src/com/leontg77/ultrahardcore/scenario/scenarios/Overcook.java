package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * Overcook scenario class.
 * 
 * @author LeonTG77
 */
public class Overcook extends Scenario implements Listener {
    
    public Overcook() {
        super("Overcook", "Furnaces smelt the whole stack of items in 10 seconds, but the furnace creates a large explosion, simulating the stress of cooking that much stuff. The explosion can hurt.");
    }

    @EventHandler
    public void on(FurnaceSmeltEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Block block = event.getBlock();
        Location loc = block.getLocation();

        if (!game.getWorlds().contains(loc.getWorld())) {
            return;
        }

        new BukkitRunnable() {
            public void run() {
                ItemStack toDrop = event.getResult().clone();
                toDrop.setAmount(toDrop.getAmount() + event.getSource().getAmount());

                Furnace furnace = (Furnace) block.getState();
                furnace.getInventory().clear();

                block.setType(Material.AIR);
                loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 5, false, true);

                BlockUtils.dropItem(loc, toDrop);
            }
        }.runTaskLater(plugin, 1);
    }
}