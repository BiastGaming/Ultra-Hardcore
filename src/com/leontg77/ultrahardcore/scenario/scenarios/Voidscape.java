package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.leontg77.ultrahardcore.scenario.GeneratorScenario;

/**
 * Voidscape scenario class.
 * 
 * @author LeonTG77
 */
public class Voidscape extends GeneratorScenario {

    public Voidscape() {
        super(
            "Voidscape", 
            "All stone and bedrock is replaced with air, to get cobble you need to make water flow to lava and make a cobblestone generator.", 
            ChatColor.BLUE, 
            "voidscape", 
            true, 
            true,
            false,
            false
        );
    }

    @Override
    public void handleBlock(Block block) {
        if (block.getType() == Material.STONE || block.getType() == Material.BEDROCK || block.getType() == Material.MONSTER_EGGS) {
            block.setType(Material.AIR);
        }
    }
}