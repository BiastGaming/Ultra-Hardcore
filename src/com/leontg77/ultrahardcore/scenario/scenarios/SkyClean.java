package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * SkyClean scenario class
 * 
 * @author LeonTG77
 */
public class SkyClean extends Scenario implements Listener {

    public SkyClean() {
        super("SkyClean", "Sand drops glass and snow blocks drop snowblocks rather than snowballs.");
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getType() == Material.SAND) {
            BlockUtils.blockBreak(player, block);
            BlockUtils.degradeDurabiliy(player);
            BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.GLASS));

            event.setCancelled(true);
            block.setType(Material.AIR);
            return;
        }

        if (block.getType() == Material.SNOW_BLOCK) {
            BlockUtils.blockBreak(player, block);
            BlockUtils.degradeDurabiliy(player);
            BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.SNOW_BLOCK));

            event.setCancelled(true);
            block.setType(Material.AIR);
        }
    }
}