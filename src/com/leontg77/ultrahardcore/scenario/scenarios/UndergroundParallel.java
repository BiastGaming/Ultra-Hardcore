package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;

import com.leontg77.ultrahardcore.scenario.GeneratorScenario;

/**
 * UndergroundParallel scenario class.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class UndergroundParallel extends GeneratorScenario {

    public UndergroundParallel() {
        super("UndergroundParallel", "The surface is replicated underground beneath y42.", ChatColor.BLUE, "underpara", true, true, false, false);
    }

    @Override
    public void handleBlock(Block block) {
        Location loc = block.getLocation();
        
        if (loc.getBlockY() == 0 || loc.getBlockY() > 42) {
            return;
        }
            
        World world = block.getWorld();
        Block surface = world.getBlockAt(block.getX(), block.getY() + 59, block.getZ());

        switch (surface.getType()) {
        case LEAVES:
        case LEAVES_2:
            double randomPerLogs = rand.nextDouble() * 100;

            if (randomPerLogs < 50.0D) {
                block.setType(Material.STONE);
                break;
            }
            randomPerLogs -= 50.0D;

            if (randomPerLogs < 24.5D) {
                block.setType(Material.COAL_ORE);
                break;
            }
            randomPerLogs -= 24.5D;

            if (randomPerLogs < 23.5D) {
                block.setType(Material.IRON_ORE);
                break;
            }
            randomPerLogs -= 23.5D;

            if (randomPerLogs < 1.0D) {
                block.setType(Material.GOLD_ORE);
                break;
            }
            randomPerLogs -= 1.0D;

            if (randomPerLogs < 0.5D) {
                block.setType(Material.LAPIS_ORE);
                break;
            }

            block.setType(Material.DIAMOND_ORE);
            break;
        case LOG:
        case LOG_2:
            double randomPerLeaves = rand.nextDouble() * 100;

            if (randomPerLeaves < 5.0D) {
                block.setType(Material.REDSTONE_ORE);
                break;
            }
            randomPerLeaves -= 5.0D;

            if (randomPerLeaves < 5.0D) {
                block.setType(Material.GLOWSTONE);
                break;
            }

            block.setType(Material.GRAVEL);
            break;
        default:
            block.setType(surface.getType());
            block.setData(surface.getData());

            if (surface.getState() instanceof InventoryHolder) {
                InventoryHolder surfaceInv = (InventoryHolder) surface.getState();
                InventoryHolder inv = (InventoryHolder) block.getState();

                inv.getInventory().setContents(surfaceInv.getInventory().getContents());
            }
            break;
        }
    }
}
