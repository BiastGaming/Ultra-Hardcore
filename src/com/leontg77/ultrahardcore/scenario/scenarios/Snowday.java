package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.GeneratorScenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * Snowday scenario class.
 * 
 * @author LeonTG77
 */
public class Snowday extends GeneratorScenario implements Listener {

    public Snowday() {
        super(
            "Snowday", 
            "Grass and dirt is replaced by snow blocks, The entire world is a cold taiga biome which snows in all the time. Also leaves drop sugar canes 2% of the time.", 
            ChatColor.WHITE, 
            "snowday", 
            false, 
            false,
            false,
            true
        );
    }
    
    @Override
    public void onEnable() {
        if (!game.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());
    }

    @EventHandler
    public void on(GameStartEvent event) {
        for (World world : game.getWorlds()) {
            if (world.getEnvironment() != Environment.NORMAL) {
                continue;
            }

            world.setThundering(false);
            world.setStorm(true);
        }
    }

    @EventHandler
    public void on(LeavesDecayEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Block block = event.getBlock();
        Location loc = block.getLocation().add(0.5, 0.7, 0.5);

        if (rand.nextInt(100) < 2) {
            BlockUtils.dropItem(loc, new ItemStack(Material.SUGAR_CANE, 1 + rand.nextInt(1)));
        }
    }

    @EventHandler
    public void on(BlockFormEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        BlockState block = event.getNewState();
        
        if (block.getType() != Material.SNOW) {
            return;
        }
        
        event.setCancelled(true);
    }

    @Override
    public void handleBlock(Block block) {
        block.setBiome(Biome.COLD_TAIGA);

        switch (block.getType()) {
        case GRASS:
        case DIRT:
            block.setType(Material.SNOW_BLOCK);
            break;
        case WATER:
        case STATIONARY_WATER:
            if (block.getRelative(BlockFace.UP).getType() == Material.AIR) {
                block.setType(Material.ICE);
            }
            break;
        case LEAVES:
        case LEAVES_2:
            if (block.getRelative(BlockFace.UP).getType() == Material.AIR) {
                block.getRelative(BlockFace.UP).setType(Material.SNOW);
            }
            break;
        default:
            break;
        }
    }
}