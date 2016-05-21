package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * CarrotCombo scenario class
 * 
 * @author LeonTG77
 */
public class Cobblehaters extends Scenario implements Listener {

    public Cobblehaters() {
        super("Cobblehaters", "You cannot mine stone, furnaces, or make stone tools.");
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockBreakEvent event)  {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        switch (block.getType()) {
        case STONE:
        case COBBLESTONE:
            player.sendMessage(ChatColor.RED + "You can't mine " + block.getType().name().toLowerCase().replace("_", " ") + " in this scenario.");
            event.setCancelled(true);
            break;
        case FURNACE:
        case BURNING_FURNACE:
            player.sendMessage(ChatColor.RED + "You can't mine " + block.getType().name().toLowerCase().replace("_", " ") + "s in this scenario.");
            event.setCancelled(true);
            break;
        default:
            break;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(final CraftItemEvent event)  {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        final ItemStack result = event.getInventory().getResult();
        final Player player = (Player) event.getWhoClicked();
        
        switch (result.getType()) {
        case STONE_SWORD:
        case STONE_PICKAXE:
        case STONE_AXE:
        case STONE_SPADE:
        case STONE_HOE:
            player.sendMessage(ChatColor.RED + "You can't craft " + result.getType().name().toLowerCase().replace("_", " ") + "s in this scenario.");
            event.setCancelled(true);
            break;
        default:
            break;
        }
    }
}