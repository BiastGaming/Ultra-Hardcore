package com.leontg77.ultrahardcore.feature.world;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;

import com.leontg77.ultrahardcore.feature.Feature;

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
    public void on(ChunkPopulateEvent event) {
        World world = event.getWorld();
        Chunk chunk = event.getChunk();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Block block = chunk.getBlock(x, 0, z);
                block = world.getHighestBlockAt(block.getLocation()).getRelative(BlockFace.UP);

                if (block.getBiome() != Biome.FOREST && block.getBiome() != Biome.FOREST_HILLS) {
                    return;
                }

                if (rand.nextInt(100) >= 30) {
                    return;
                }

                Location loc = block.getLocation();
                loc.getWorld().generateTree(loc, TreeType.BIG_TREE);
            }
        }
    }
}