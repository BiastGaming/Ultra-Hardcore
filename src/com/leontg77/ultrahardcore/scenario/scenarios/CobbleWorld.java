package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.leontg77.ultrahardcore.scenario.GeneratorScenario;

/**
 * CobbleWorld scenario class.
 * 
 * @author LeonTG77
 */
public class CobbleWorld extends GeneratorScenario {

    public CobbleWorld() {
        super(
            "CobbleWorld", 
            "All stone is replaced with cobble stone.", 
            ChatColor.GRAY, 
            "cobble", 
            false, 
            false, 
            false, 
            false
        );
    }

    @Override
    public void handleBlock(Block block) {
        if (block.getType() == Material.STONE) {
            block.setType(Material.COBBLESTONE);
        }
    }
}