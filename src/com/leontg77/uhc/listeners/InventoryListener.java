package com.leontg77.uhc.listeners;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.Game;
import com.leontg77.uhc.InvGUI;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Inventory listener class.
 * <p> 
 * Contains all eventhandlers for inventory releated events.
 * 
 * @author LeonTG77
 */
public class InventoryListener implements Listener {
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		Inventory inv = event.getInventory();
		Arena arena = Arena.getInstance();
		
		if (arena.isEnabled() && inv instanceof EnchantingInventory) {
			inv.setItem(1, new ItemStack (Material.INK_SACK, 3, (short) 4));
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Inventory inv = event.getInventory();
		Arena arena = Arena.getInstance();

		if (arena.isEnabled() && inv instanceof EnchantingInventory) {
			inv.setItem(1, null);
		}
		
		if (Main.invsee.containsKey(inv)) {
			Main.invsee.get(inv).cancel();
			Main.invsee.remove(inv);
		}
		
		if (Main.rules.containsKey(inv)) {
			Main.rules.get(inv).cancel();
			Main.rules.remove(inv);
		}
	}

	@EventHandler
	public void onEnchantItem(EnchantItemEvent event) {
		Inventory inv = event.getInventory();
		Arena arena = Arena.getInstance();
		
		if (arena.isEnabled()) {
			inv.setItem(1, new ItemStack (Material.INK_SACK, 3, (short) 4));
		}
	}
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {	
        if (event.getCurrentItem() == null) {
        	return;
        }
        
		Player player = (Player) event.getWhoClicked();
		Arena arena = Arena.getInstance();
		
		Inventory inv = event.getInventory();
		ItemStack item = event.getCurrentItem();
		
		if (arena.isEnabled() && event.getClickedInventory() instanceof EnchantingInventory) {
			if (event.getSlot() == 1) {
				event.setCancelled(true);
			}
		}
		
		if (inv.getTitle().endsWith("'s Inventory")) {
			event.setCancelled(true);
			return;
		}
		
		if (inv.getTitle().endsWith("'s Hall of Fame �8�")) {
			InvGUI manager = InvGUI.getManager();
			
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
				if (item.getItemMeta().getDisplayName().equalsIgnoreCase("�aNext page")) {
					manager.currentPage.put(player, manager.currentPage.get(player) + 1);
					player.openInventory(manager.pagesForPlayer.get(player).get(manager.currentPage.get(player)));
				}
				
				if (item.getItemMeta().getDisplayName().equalsIgnoreCase("�aPrevious page")) {
					manager.currentPage.put(player, manager.currentPage.get(player) - 1); 
					player.openInventory(manager.pagesForPlayer.get(player).get(manager.currentPage.get(player)));
				}
			}
			
			event.setCancelled(true);
			return;
		}
		
		if (inv.getTitle().equals("�8� �cGame Information �8�")) {
			event.setCancelled(true);
			return;
		}
		
		if (Spectator.getManager().isSpectating(player)) {
			if (inv.getTitle().equals("�8� �cPlayer Selector �8�")) {
				if (!event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
					return;
				}
				
				Player target = Bukkit.getServer().getPlayer(event.getCurrentItem().getItemMeta().getDisplayName().substring(2, event.getCurrentItem().getItemMeta().getDisplayName().length()));
				
				if (target == null) {
					player.sendMessage(Main.prefix() + "The player you clicked is not online.");
				} 
				else {
					player.teleport(target);
				}
				
				player.closeInventory();
				event.setCancelled(true);
				return;
			}
			
			if (item.getType() == Material.COMPASS) {
				if (event.isLeftClick()) {
					ArrayList<Player> players = new ArrayList<Player>();
					for (Player online : PlayerUtils.getPlayers()) {
						if (!Spectator.getManager().isSpectating(online)) {
							players.add(online);
						}
					}
					
					if (players.size() > 0) {
						Player target = players.get(new Random().nextInt(players.size()));
						player.teleport(target.getLocation());
						player.sendMessage(Main.prefix() + "You teleported to �a" + target.getName() + "�7.");
					} 
					else {
						player.sendMessage(Main.prefix() + "No players to teleport to.");
					}
					
					event.setCancelled(true);
					return;
				}
				
				InvGUI.getManager().openSelector(player);
				event.setCancelled(true);
				return;
			}
			
			if (item.getType() == Material.INK_SACK) {
				if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
					player.removePotionEffect(PotionEffectType.NIGHT_VISION);
				} 
				else {
					player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 10000000, 0));
				}
				
				event.setCancelled(true);
				return;
			}
			
			if (item.getType() == Material.LAVA_BUCKET) {
				if (!Game.getInstance().nether()) {
					player.sendMessage(Main.prefix() + "Nether is disabled.");
					event.setCancelled(true);
					return;
				}

				ArrayList<String> netherL = new ArrayList<String>();
				StringBuilder nether = new StringBuilder();
				
				for (Player online : PlayerUtils.getPlayers()) {
					if (online.getWorld().getEnvironment() == Environment.NETHER) {
						if (Spectator.getManager().isSpectating(online)) {
							continue;
						}
						
						netherL.add(online.getName());
					}
				}
				
				if (netherL.size() == 0) {
					player.sendMessage(Main.prefix() + "No players are in the nether.");
					event.setCancelled(true);
					return;
				}
				
				int i = 1;
				
				for (Player online : PlayerUtils.getPlayers()) {
					if (online.getWorld().getEnvironment() == Environment.NETHER) {
						if (Spectator.getManager().isSpectating(online)) {
							continue;
						}
						
						if (nether.length() > 0) {
							if (netherL.size() == i) {
								nether.append(" �7and �a");
							} else {
								nether.append("�7, �a");
							}
						}

						nether.append("�a" + online.getName());
						i++;
					}
				}

				player.sendMessage(Main.prefix() + "Players in the nether:");
				player.sendMessage("�8� �7" + nether.toString().trim());
				
				event.setCancelled(true);
				return;
			}
			
			if (item.getType() == Material.FEATHER) {
				player.teleport(new Location(player.getWorld(), 0.5, 100, 0.5));
				event.setCancelled(true);
			}
		}
	}
}