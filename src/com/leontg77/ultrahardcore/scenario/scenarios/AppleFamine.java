package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * AppleFamine scenario class.
 * 
 * @author LeonTG77
 */
public class AppleFamine extends Scenario implements Listener {

    public AppleFamine() {
        super("AppleFamine", "Apples do not drop, meaning you can only heal with potions and golden heads.");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(LeavesDecayEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Block block = event.getBlock();
        
        block.setType(Material.AIR);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(BlockBreakEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        BlockUtils.blockBreak(player, block);
        BlockUtils.degradeDurabiliy(player);
        
        block.setType(Material.AIR);
        event.setCancelled(true);
    }
}