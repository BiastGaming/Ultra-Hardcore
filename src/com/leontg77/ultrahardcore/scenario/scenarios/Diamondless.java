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
 * Diamondless scenario class
 * 
 * @author LeonTG77
 */
public class Diamondless extends Scenario implements Listener {
    private final CutClean cc;

    public Diamondless(CutClean cc) {
        super("Diamondless", "Mining diamond ore will result in dropping iron.");

        this.cc = cc;
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getType() != Material.DIAMOND_ORE) {
            return;
        }

        ItemStack replace = new ItemStack(cc.isEnabled() ? Material.IRON_INGOT : Material.IRON_ORE);

        BlockUtils.blockBreak(player, block);
        BlockUtils.degradeDurabiliy(player);
        BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), replace);

        event.setCancelled(true);
        block.setType(Material.AIR);
    }
}