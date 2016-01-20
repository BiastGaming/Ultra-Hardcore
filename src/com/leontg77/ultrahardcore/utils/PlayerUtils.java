package com.leontg77.ultrahardcore.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.leontg77.ultrahardcore.Main;

/**
 * Player utilities class.
 * <p>
 * Contains player related methods.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class PlayerUtils {
	
	/**
	 * Get a list of players online.
	 * 
	 * @return A list of online players.
	 */
	public static List<Player> getPlayers() {
		return new ArrayList<Player>(Bukkit.getOnlinePlayers());
	}
	
	/**
	 * Get the given player's ping.
	 * 
	 * @param player the player
	 * @return the players ping
	 */
	public static int getPing(Player player) {
		CraftPlayer craft = (CraftPlayer) player;
		return craft.getHandle().ping;
	} 
	
	/**
	 * Gets an offline player by a name.
	 * <p>
	 * This is just because of the deprecation on <code>Bukkit.getOfflinePlayer(String)</code> 
	 * 
	 * @param name The name.
	 * @return the offline player.
	 */
	public static OfflinePlayer getOfflinePlayer(String name) {
		return Bukkit.getOfflinePlayer(name);
	}
	
	/**
	 * Broadcasts a message to everyone online.
	 * 
	 * @param message the message.
	 */
	public static void broadcast(String message) {
		broadcast(message, null);
	}
	
	/**
	 * Broadcasts a message to everyone online with a specific permission.
	 * 
	 * @param message the message.
	 * @param permission the permission.
	 */
	public static void broadcast(String message, String permission) {
		for (Player online : getPlayers()) {
			if (permission != null && !online.hasPermission(permission)) {
				continue;
			}
			
			online.sendMessage(message);
		}
		
		String consoleMsg = message;

		consoleMsg = consoleMsg.replaceAll("§l", "");
		consoleMsg = consoleMsg.replaceAll("§o", "");
		consoleMsg = consoleMsg.replaceAll("§r", "§f");
		consoleMsg = consoleMsg.replaceAll("§m", "");
		consoleMsg = consoleMsg.replaceAll("§n", "");
		
		Bukkit.getLogger().info(consoleMsg);
	}

	/**
	 * Damage the given player by the given amount
	 * <p>
	 * This will also call the damage event.
	 * 
	 * @param player The player to damage.
	 * @param amount The amount of damage.
	 */
	public static void damage(Player player, double amount) {
		final EntityDamageEvent event = new EntityDamageEvent(player, DamageCause.CUSTOM, amount);
		
		Bukkit.getPluginManager().callEvent(event);
		player.damage(amount);
	}
	
	/**
	 * Get a list of entites within a distance of a location.
	 * 
	 * @param loc the location.
	 * @param distance the distance.
	 * @return A list of entites nearby.
	 */
	public static List<Entity> getNearby(Location loc, double distance) {
		List<Entity> list = new ArrayList<Entity>();
		
		for (Entity e : loc.getWorld().getEntities()) {
			if (e instanceof Player) {
				continue;
			}
			
			if (!e.getType().isAlive()) {
				continue;
			}
			
			if (loc.distance(e.getLocation()) <= distance) {
				list.add(e);
			}
		}
		
		for (Player online : getPlayers()) {
			if (online.getWorld() == loc.getWorld()) {
				if (loc.distance(online.getLocation()) <= distance) {
					list.add(online);
				}
			}
		}
		
		return list;
	}
	
	/**
	 * Give the given item to the given player.
	 * <p>
	 * Method is made so if the inventory is full it drops the item to the ground.
	 * 
	 * @param player the player giving to.
	 * @param stack the item giving.
	 */
	public static void giveItem(Player player, ItemStack stack) {
		PlayerInventory inv = player.getInventory();
		
		HashMap<Integer, ItemStack> leftOvers = inv.addItem(stack);
		
		if (leftOvers.isEmpty()) {
			return;
		}
		
		player.sendMessage(Main.PREFIX + "Your inventory was full, item was dropped on the ground.");
		Location loc = player.getLocation();
		
		for (ItemStack leftOver : leftOvers.values()) {
			BlockUtils.dropItem(loc, leftOver);
		}
	}
}