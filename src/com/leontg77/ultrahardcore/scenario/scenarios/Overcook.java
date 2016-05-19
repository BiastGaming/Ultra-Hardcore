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

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * Overcook scenario class.
 * 
 * @author LeonTG77
 */
public class Overcook extends Scenario implements Listener {
    private final Main plugin;
    private final Game game;

    /**
     * Overcook scenario class constructor.
     */
    public Overcook(Main plugin, Game game) {
        super("Overcook", "Furnaces smelt the whole stack of items in 10 seconds, but the furnace creates a large explosion, simulating the stress of cooking that much stuff. The explosion can hurt.");

        this.plugin = plugin;
        this.game = game;
    }

    @EventHandler
    public void on(final FurnaceSmeltEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        final Block block = event.getBlock();
        final Location loc = block.getLocation();

        if (!game.getWorlds().contains(loc.getWorld())) {
            return;
        }

        new BukkitRunnable() {
            public void run() {
                ItemStack toDrop = event.getResult().clone();
                toDrop.setAmount(toDrop.getAmount() + event.getSource().getAmount());

                Furnace fur = (Furnace) block.getState();
                fur.getInventory().clear();

                block.setType(Material.AIR);
                loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 5, false, true);

                BlockUtils.dropItem(loc, toDrop);
            }
        }.runTaskLater(plugin, 1);
    }
}