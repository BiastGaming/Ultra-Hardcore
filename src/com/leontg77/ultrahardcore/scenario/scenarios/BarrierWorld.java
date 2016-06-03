package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

import com.leontg77.ultrahardcore.scenario.GeneratorScenario;

/**
 * BarrierWorld scenario class.
 *
 * @author D4mnX
 */
public class BarrierWorld extends GeneratorScenario implements CommandExecutor, Listener {
    
    public BarrierWorld() {
        super(
            "BarrierWorld",
            "Stone, dirt and grass is replaced with barriers. Stone with 2 non-slid blocks above is unaffected, allowing you to see caves.", 
            ChatColor.WHITE,
            "barrierworld", 
            false, 
            false, 
            false, 
            false
        );
    }

    @Override
    public void handleBlock(Block block) {
        Block oneAbove = block.getRelative(BlockFace.UP);
        Block twoAbove = oneAbove.getRelative(BlockFace.UP);

        if (block.getType() == Material.DIRT || block.getType() == Material.GRASS) {
            block.setType(Material.BARRIER);
            return;
        }

        if (block.getType() != Material.STONE) {
            return;
        }

        if (!oneAbove.getType().isSolid() && !twoAbove.getType().isSolid()) {
            return;
        }

        block.setType(Material.BARRIER);
    }
}