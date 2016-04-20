package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Bowless scenario class.
 * 
 * @author LeonTG77
 */
public class Bowless extends Scenario implements Listener {

	/**
	 * Bowless class constructor.
	 */
	public Bowless() {
		super("Bowless", "You cannot get or use bows.");
	}

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerPickupItemEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
        
        Item item = event.getItem();
        ItemStack stack = item.getItemStack();
        
        if (stack.getType() == Material.BOW) {
        	event.setCancelled(true);
        	item.remove();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryClickEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
        
        ItemStack item = event.getCurrentItem();
        
        if (item == null) {
            return;
        }

        if (item.getType() == Material.BOW) {
            Player player = (Player) event.getWhoClicked();
            
            event.setCurrentItem(new ItemStack(Material.AIR));
            event.setCancelled(true);
            
            player.sendMessage(ChatColor.RED + "Bows are disabled in Bowless!");
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void on(CraftItemEvent event)  {
		if (!State.isState(State.INGAME)) {
			return;
		}
        
        ItemStack item = event.getCurrentItem();
        
        // never tested if this would happend, but incase..
        if (item == null) {
            return;
        }

        if (item.getType() == Material.BOW) {
            Player player = (Player) event.getWhoClicked();

            player.sendMessage(ChatColor.RED + "Bows are disabled in Bowless!");
            event.setCancelled(true);
        }
    }
}