package com.leontg77.ultrahardcore.feature.world;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.leontg77.ultrahardcore.events.ChunkModifiableEvent;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.utils.LocationUtils;

/**
 * Big Trees In Forests feature class.
 * 
 * @author LeonTG77
 */
public class BigTreesInForestsFeature extends Feature implements Listener {
    private final Random rand = new Random();

    public BigTreesInForestsFeature() {
        super("Big Trees In Forests", "Makes big trees appeal in forest biomes.");
    }

    @EventHandler
    public void on(ChunkModifiableEvent event) {
        Chunk chunk = event.getChunk();

        int x = rand.nextInt(16);
        int z = rand.nextInt(16);

        Block block = chunk.getBlock(x, 60, z);
        
        if (block.getBiome() != Biome.FOREST && block.getBiome() != Biome.FOREST_HILLS) {
            return;
        }
        
        block = LocationUtils.getHighestBlock(block.getLocation()).getBlock();
        
        if (block.getType() != Material.GRASS) {
            return;
        }
        
        block = block.getRelative(BlockFace.UP);

        if (rand.nextBoolean()) {
            return;
        }

        Location loc = block.getLocation();
        loc.getWorld().generateTree(loc, TreeType.BIG_TREE);
    }
}