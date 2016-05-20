package com.leontg77.ultrahardcore.feature.world;

import java.util.Random;

import com.leontg77.ultrahardcore.feature.Feature;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Biomebased Wood feature class.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class BiomebasedWoodFeature extends Feature implements Listener {
    private final Random rand;
    private final Main plugin;
    
    public BiomebasedWoodFeature(Main plugin) {
        super("Biomebased Wood", "Makes all natural wood in the world be the type that fits the biome.");

        this.rand = new Random();
        this.plugin = plugin;
    }
    
    private boolean physics = true
    
    @EventHandler
    public void on(BlockPhysicsEvent event) {
        if (!physics) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void on(ChunkPopulateEvent event) {
        final Chunk chunk = event.getChunk();

        new BukkitRunnable() {
            public void run() {
                physics = false; // for doors not to pop off during setting
                
                for (int y = 0; y < 256; y++) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 17; z++) {
                            handleBlock(chunk.getBlock(x, y, z));
                        }
                    }
                }
                
                physics = true;
            }
        }.runTaskLater(plugin, 20);
    }

    /**
     * Handle the given block to the right wood type.
     * 
     * @param block The block to handle
     */
    private void handleBlock(Block block) {
        Biome biome = block.getBiome();
        byte oldData = block.getData();

        switch (biome) {
        case BIRCH_FOREST:
        case BIRCH_FOREST_HILLS:
        case BIRCH_FOREST_HILLS_MOUNTAINS:
        case BIRCH_FOREST_MOUNTAINS:
            switch (block.getType()) {
            case LOG:
            case WOOD:
                block.setData((byte) 2);
                block.getState().update(true);
                break;
            case FENCE:
                block.setType(Material.BIRCH_FENCE);
                block.setData(oldData);
                break;
            case FENCE_GATE:
                block.setType(Material.BIRCH_FENCE_GATE);
                block.setData(oldData);
                break;
            case WOODEN_DOOR:
                block.setType(Material.BIRCH_DOOR);
                block.setData(oldData);
                break;
            case WOOD_STAIRS:
                block.setType(Material.BIRCH_WOOD_STAIRS);
                block.setData(oldData);
                break;
            default:
                break;
            }
            break;
        case JUNGLE:
        case JUNGLE_EDGE:
        case JUNGLE_EDGE_MOUNTAINS:
        case JUNGLE_HILLS:
        case JUNGLE_MOUNTAINS:
            switch (block.getType()) {
            case LOG:
            case WOOD:
                block.setData((byte) 3);
                block.getState().update(true);
                break;
            case FENCE:
                block.setType(Material.JUNGLE_FENCE);
                block.setData(oldData);
                break;
            case FENCE_GATE:
                block.setType(Material.JUNGLE_FENCE_GATE);
                block.setData(oldData);
                break;
            case WOODEN_DOOR:
                block.setType(Material.JUNGLE_DOOR);
                block.setData(oldData);
                break;
            case WOOD_STAIRS:
                block.setType(Material.JUNGLE_WOOD_STAIRS);
                block.setData(oldData);
                break;
            default:
                break;
            }
            break;
        case ROOFED_FOREST:
        case ROOFED_FOREST_MOUNTAINS:
            switch (block.getType()) {
            case LOG:
                block.setType(Material.LOG_2);
                block.setData((byte) 1);
                block.getState().update(true);
                break;
            case LOG_2:
                block.setData((byte) 1);
                block.getState().update(true);
                break;
            case WOOD:
                block.setData((byte) 5);
                block.getState().update(true);
                break;
            case FENCE:
                block.setType(Material.DARK_OAK_FENCE);
                block.setData(oldData);
                break;
            case FENCE_GATE:
                block.setType(Material.DARK_OAK_FENCE_GATE);
                block.setData(oldData);
                break;
            case WOODEN_DOOR:
                block.setType(Material.DARK_OAK_DOOR);
                block.setData(oldData);
                break;
            case WOOD_STAIRS:
                block.setType(Material.DARK_OAK_STAIRS);
                block.setData(oldData);
                break;
            default:
                break;
            }
            break;
        case SAVANNA:
        case SAVANNA_MOUNTAINS:
        case SAVANNA_PLATEAU:
        case SAVANNA_PLATEAU_MOUNTAINS:
            switch (block.getType()) {
            case LOG:
                block.setType(Material.LOG_2);
                block.setData((byte) 0);
                block.getState().update(true);
                break;
            case LOG_2:
                block.setData((byte) 0);
                block.getState().update(true);
                break;
            case WOOD:
                block.setData((byte) 4);
                block.getState().update(true);
                break;
            case FENCE:
                block.setType(Material.ACACIA_FENCE);
                block.setData(oldData);
                break;
            case FENCE_GATE:
                block.setType(Material.ACACIA_FENCE_GATE);
                block.setData(oldData);
                break;
            case WOODEN_DOOR:
                block.setType(Material.ACACIA_DOOR);
                block.setData(oldData);
                break;
            case WOOD_STAIRS:
                block.setType(Material.ACACIA_STAIRS);
                block.setData(oldData);
                break;
            default:
                break;
            }
            break;
        case TAIGA:
        case TAIGA_HILLS:
        case TAIGA_MOUNTAINS:
        case MEGA_SPRUCE_TAIGA:
        case MEGA_SPRUCE_TAIGA_HILLS:
        case MEGA_TAIGA:
        case MEGA_TAIGA_HILLS:
        case COLD_TAIGA:
        case COLD_TAIGA_HILLS:
        case COLD_TAIGA_MOUNTAINS:
            switch (block.getType()) {
            case LOG:
            case WOOD:
                block.setData((byte) 1);
                block.getState().update(true);
                break;
            case FENCE:
                block.setType(Material.SPRUCE_FENCE);
                block.setData(oldData);
                break;
            case FENCE_GATE:
                block.setType(Material.SPRUCE_FENCE_GATE);
                block.setData(oldData);
                break;
            case WOODEN_DOOR:
                block.setType(Material.SPRUCE_DOOR);
                block.setData(oldData);
                break;
            case WOOD_STAIRS:
                block.setType(Material.SPRUCE_WOOD_STAIRS);
                block.setData(oldData);
                break;
            default:
                break;
            }
            break;
        case DESERT:
        case DESERT_HILLS:
        case DESERT_MOUNTAINS:
            switch (block.getType()) {
            case WOOD:
                block.setType(Material.SANDSTONE);
                break;
            case FENCE:
                block.setType(Material.BIRCH_FENCE);
                block.setData(oldData);
                break;
            case FENCE_GATE:
                block.setType(Material.BIRCH_FENCE_GATE);
                block.setData(oldData);
                break;
            case WOODEN_DOOR:
                block.setType(Material.BIRCH_DOOR);
                block.setData(oldData);
                break;
            case WOOD_STAIRS:
                block.setType(Material.SANDSTONE_STAIRS);
                block.setData(oldData);
                break;
            default:
                break;
            }
            break;
        default:
            break;
        }
    }
}
