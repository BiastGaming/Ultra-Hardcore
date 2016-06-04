package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.leontg77.ultrahardcore.scenario.GeneratorScenario;

/**
 * ChunkApocalypse scenario class
 * 
 * @author LeonTG77
 */
public class ChunkApocalypse extends GeneratorScenario {

    public ChunkApocalypse() {
        super(
            "ChunkApocalypse", 
            "Every chunk has a 30% chance of being replaced with air", 
            ChatColor.AQUA, 
            "chunkapo", 
            true, 
            true, 
            false, 
            false
        );
    }

    @Override
    public void handleBlock(Block block) {}
    
    @Override
    public void handleChunk(Chunk chunk) {
        if (rand.nextInt(100) >= 30) {
            return;
        }
        
        for (int y = 256; y >= 0; y--) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    Block block = chunk.getBlock(x, y, z);
                    block.setType(Material.AIR);
                }
            }
        }
    }
}