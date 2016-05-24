package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Landmines scenario class
 * 
 * @author LeonTG77
 */
public class Landmines extends Scenario implements Listener {
    private final SpecManager spec;

    public Landmines(SpecManager spec) {
        super("Landmines", "When walked on, a block deteriorates. Grass, when stepped on, turns into stone. Stone, when stepped on turns into cobble. Cobble, when stepped on twice turns into netherrack. Netherrack, when stepped on ONCE, explodes, dealing 3-6 hearts of damage");
        
        this.spec = spec;
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();

        if (spec.isSpectating(player)) {
            return;
        }

        if (!game.getWorlds().contains(event.getTo().getWorld())) {
            return;
        }

        final Block block = event.getTo().clone().add(0, -1, 0).getBlock();

        if (block.getType() == Material.GRASS) {
            new BukkitRunnable() {
                public void run() {
                    block.setType(Material.STONE);
                }
            }.runTaskLater(plugin, 5);
            return;
        }

        if (block.getType() == Material.STONE) {
            new BukkitRunnable() {
                public void run() {
                    block.setType(Material.COBBLESTONE);
                }
            }.runTaskLater(plugin, 5);
            return;
        }

        if (block.getType() == Material.COBBLESTONE) {
            new BukkitRunnable() {
                public void run() {
                    block.setType(Material.NETHERRACK);
                }
            }.runTaskLater(plugin, 5);
            return;
        }

        if (block.getType() == Material.NETHERRACK) {
            Location loc = block.getLocation();
            block.setType(Material.AIR);

            block.getWorld().createExplosion(loc.getX() + 0.5, loc.getY() + 0.5, loc.getZ() + 0.5, 10, false, true);
        }
    }
}