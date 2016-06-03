package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.function.Predicate;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.leontg77.ultrahardcore.scenario.GeneratorScenario;

/**
 * SkyOres scenario class.
 * 
 * @author LeonTG77
 */
public class SkyOres extends GeneratorScenario {

    public SkyOres() {
        super(
            "SkyOres", 
            "Instead of in caves, ores are in the sky in the inverted way they would be normally. This means diamonds will be high up and coal will be low.", 
            ChatColor.WHITE, 
            "skyores", 
            false, 
            false,
            false, 
            false
        );
    }
    
    private static final Predicate<Material> IS_ORE = (m) -> m.name().endsWith("_ORE");

    @Override
    public void handleBlock(Block block) {
        Material type = block.getType();
        
        if (!IS_ORE.test(type)) {
            return;
        }

        block.getWorld().getBlockAt(block.getX(), 256 - block.getY(), block.getZ()).setType(type);
        block.setType(Material.STONE);
    }
}