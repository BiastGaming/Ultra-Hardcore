package com.leontg77.ultrahardcore.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.portal.NetherFeature;
import com.leontg77.ultrahardcore.inventory.InvGUI;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.utils.LocationUtils;

/**
 * Spectator inventory listener class.
 * <p> 
 * Contains all eventhandlers for spectator inventory releated events.
 * 
 * @author LeonTG77
 */
public class SpectatorListener implements Listener {
	private final Game game;

	private final SpecManager spec;
	private final InvGUI inv;
	
	private final NetherFeature nether;
	
	/**
	 * Spectator listener class constructor.
	 * 
	 * @param game The game class.
	 * @param spec The spectator manager class.
	 * @param inv The inv gui class.
	 * @param nether The nether feature class.
	 */
	public SpectatorListener(Game game, SpecManager spec, InvGUI inv, NetherFeature nether) {
		this.game = game;
		
		this.spec = spec;
		this.inv = inv;
		
		this.nether = nether;
	}

	@EventHandler
    public void on(PlayerInteractEvent event) {	
        Player player = event.getPlayer();
        Action action = event.getAction();
        
		if (!spec.isSpectating(player)) {
			return;
		}
		
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			inv.openSelector(game, player);
			return;
		} 
		
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			List<Player> list = game.getPlayers();
			
			if (list.isEmpty()) {
				player.sendMessage(Main.PREFIX + "Couldn't find any players.");
				return;
			}
			
			Random rand = new Random();
			Player target = list.get(rand.nextInt(list.size()));
			
			player.sendMessage(Main.PREFIX + "Teleported to �a" + target.getName() + "�7.");
			player.teleport(target.getLocation());
		}
	}
	
	@EventHandler
    public void on(PlayerInteractEntityEvent event) {
		Entity clicked = event.getRightClicked();
		Player player = event.getPlayer();
		
		if (!(clicked instanceof Player)) {
			return;
		}
	    	
		Player interacted = (Player) clicked;
		
		if (!spec.isSpectating(player)) {
			return;
		}
		
		if (spec.isSpectating(interacted)) {
			return;
		}
		
		inv.openPlayerInventory(player, interacted);
    }
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {	
        if (event.getCurrentItem() == null) {
        	return;
        }
        
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		
		if (!spec.isSpectating(player)) {
			return;
		}
		
		if (item.getType() == Material.INK_SACK) {
			if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
				player.removePotionEffect(PotionEffectType.NIGHT_VISION);
			} else {
				player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1726272000, 0));
			}
			
			event.setCancelled(true);
			return;
		}
		
		if (item.getType() == Material.FEATHER) {
			Location loc = new Location(player.getWorld(), 0.5, 0, 0.5);
			loc.setY(LocationUtils.highestTeleportableYAtLocation(loc) + 1);
			
			player.teleport(loc);
			event.setCancelled(true);
			return;
		}
		
		if (item.getType() == Material.COMPASS) {
			if (event.isRightClick()) {
				inv.openSelector(game, player);
				event.setCancelled(true);
				return;
			}
			
			ArrayList<Player> players = new ArrayList<Player>();
			
			for (Player online : Bukkit.getOnlinePlayers()) {
				if (!spec.isSpectating(online)) {
					players.add(online);
				}
			}
			
			if (players.size() > 0) {
				Player target = players.get(new Random().nextInt(players.size()));
				player.teleport(target.getLocation());
				player.sendMessage(Main.PREFIX + "You teleported to �a" + target.getName() + "�7.");
			} else {
				player.sendMessage(Main.PREFIX + "No players to teleport to.");
			}
			
			event.setCancelled(true);
			return;
		}
		
		if (item.getType() == Material.LAVA_BUCKET) {
			if (!nether.isEnabled()) {
				player.sendMessage(Main.PREFIX + "Nether is disabled.");
				event.setCancelled(true);
				return;
			}

			ArrayList<String> netherL = new ArrayList<String>();
			StringBuilder nether = new StringBuilder();
			
			for (Player online : Bukkit.getOnlinePlayers()) {
				if (online.getWorld().getEnvironment() == Environment.NETHER) {
					if (spec.isSpectating(online)) {
						continue;
					}
					
					netherL.add(online.getName());
				}
			}
			
			if (netherL.size() == 0) {
				player.sendMessage(Main.PREFIX + "No players are in the nether.");
				event.setCancelled(true);
				return;
			}
			
			int i = 1;
			
			for (Player online : Bukkit.getOnlinePlayers()) {
				if (online.getWorld().getEnvironment() == Environment.NETHER) {
					if (spec.isSpectating(online)) {
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

			player.sendMessage(Main.PREFIX + "Players in the nether:");
			player.sendMessage("�8� �7" + nether.toString().trim());
			
			event.setCancelled(true);
		}
	}
}