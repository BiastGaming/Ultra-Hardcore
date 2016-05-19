package com.leontg77.test;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }
    
    Random rand = new Random();
    
    @SuppressWarnings("deprecation")
    @EventHandler
    public void on(ChunkPopulateEvent event) {
        final Chunk chunk = event.getChunk();

        new BukkitRunnable() {
            public void run() {
                for (int y = 0; y < 256; y++) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 17; z++) {
                            final Block block = chunk.getBlock(x, y, z);
                            final Biome biome = block.getBiome();
                            final byte oldData = block.getData();
                    
                            switch (biome) {
                            case BIRCH_FOREST:
                            case BIRCH_FOREST_HILLS:
                            case BIRCH_FOREST_HILLS_MOUNTAINS:
                            case BIRCH_FOREST_MOUNTAINS:
                                switch (block.getType()) {
                                case LOG:
                                case LOG_2:
                                case WOOD:
                                    handle(block, 2);
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
                                case LOG_2:
                                case WOOD:
                                    handle(block, 3);
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
                                    handle(block, 1);
                                    break;
                                case LOG_2:
                                    handle(block, 1);
                                    break;
                                case WOOD:
                                    handle(block, 5);
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
                                    handle(block, 0);
                                    break;
                                case LOG_2:
                                    handle(block, 0);
                                    break;
                                case WOOD:
                                    handle(block, 4);
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
                                case LOG_2:
                                case WOOD:
                                    handle(block, 1);
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
                            default:
                                break;
                            }
                        }
                    }
                }
            }
        }.runTaskLater(this, 20);
    }

    @SuppressWarnings("deprecation")
    private void handle(final Block block, final int newId) {
        final Material oldType = block.getType();
        block.setType(Material.STONE);
        
        new BukkitRunnable() {
            public void run() {
                block.setType(oldType);
                block.setData((byte) newId);
                block.getState().update(true);
            }
        }.runTaskLater(this, 20);
    }
}
