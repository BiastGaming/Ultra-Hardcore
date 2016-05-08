package com.leontg77.ultrahardcore.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.portal.NetherFeature;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.gui.guis.InvseeGUI;
import com.leontg77.ultrahardcore.gui.guis.SelectorGUI;
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
	private final Main plugin;
	private final Game game;

	private final SpecManager spec;
	private final GUIManager gui;
	
	private final NetherFeature nether;
	
	/**
	 * Spectator listener class constructor.
	 *
	 * @param plugin
	 * @param game The game class.
	 * @param spec The spectator manager class.
	 * @param gui The inv gui class.
	 * @param nether The nether feature class.
	 */
	public SpectatorListener(Main plugin, Game game, SpecManager spec, GUIManager gui, NetherFeature nether) {
		this.plugin = plugin;
		this.game = game;
		
		this.spec = spec;
		this.gui = gui;
		
		this.nether = nether;
	}
	
	private final Random rand = new Random();

	@EventHandler
	public void on(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		
		if (!spec.isSpectating(player)) {
			return;
		}

		if (!spec.getSpecChatProtected().contains(player.getName())) {
			return;
		}

		String message = event.getMessage();
		
		if (message.equalsIgnoreCase("gg")) {
			return;
		}
		
		event.setCancelled(true);
		Bukkit.getScheduler().runTask(plugin, () -> player.performCommand("specchat " + message));
	}

	@EventHandler
    public void on(PlayerInteractEvent event) {	
        Player player = event.getPlayer();
        Action action = event.getAction();
        
		if (!spec.isSpectating(player)) {
			return;
		}
		
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			
			if (block != null && action == Action.RIGHT_CLICK_BLOCK && block instanceof InventoryHolder) {
				return;
			}
			
			SelectorGUI sel = gui.getGUI(SelectorGUI.class);
			
			sel.currentPage.put(player.getName(), 1);
			player.openInventory(sel.get());
			return;
		} 
		
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			List<Player> list = game.getPlayers();
			
			if (list.isEmpty()) {
				player.sendMessage(Main.PREFIX + "Couldn't find any players.");
				return;
			}
			
			Player target = list.get(rand.nextInt(list.size()));
			
			player.sendMessage(Main.PREFIX + "Teleported to §a" + target.getName() + "§7.");
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
		
		player.openInventory(gui.getGUI(InvseeGUI.class).get(interacted));
    }
	
	@EventHandler
    public void on(InventoryClickEvent event) {	
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
			if (game.isMovedMiddle() && !player.hasPermission("uhc.staff")) {
				player.sendMessage(Main.PREFIX + "You need to be staff to use this in moved 0,0 games.");
				return;
			}
			
			World world = game.getWorld();
			
			if (world == null) {
				return;
			}
			
			Location center = world.getWorldBorder().getCenter();
			
			Location loc = new Location(world, center.getBlockX() + 0.5, 0, center.getBlockZ() + 0.5);
			loc.setY(LocationUtils.highestTeleportableYAtLocation(loc) + 1);
			
			player.teleport(loc);
			event.setCancelled(true);
			return;
		}
		
		if (item.getType() == Material.COMPASS) {
			if (event.isRightClick()) {
				SelectorGUI sel = gui.getGUI(SelectorGUI.class);
				
				sel.currentPage.put(player.getName(), 1);
				
				player.openInventory(sel.get());
				event.setCancelled(true);
				return;
			}
			
			List<Player> players = game.getPlayers();
			
			event.setCancelled(true);
			
			if (players.isEmpty()) {
				player.sendMessage(Main.PREFIX + "There are no players to teleport to.");
				return;
			}
			
			Player target = players.get(rand.nextInt(players.size()));
			
			player.sendMessage(Main.PREFIX + "You teleported to §a" + target.getName() + "§7.");
			player.teleport(target.getLocation());
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
							nether.append(" §7and §a");
						} else {
							nether.append("§7, §a");
						}
					}

					nether.append("§a" + online.getName());
					i++;
				}
			}

			player.sendMessage(Main.PREFIX + "Players in the nether:");
			player.sendMessage(Main.ARROW + nether.toString().trim());
			
			event.setCancelled(true);
		}
	}
}