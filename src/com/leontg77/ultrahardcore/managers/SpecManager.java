package com.leontg77.ultrahardcore.managers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.ImmutableSet;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * The spectator class to manage spectating.
 * <p>
 * This class contains methods for enabling/disabling spec mode, 
 * toggling spec mode and managing vanishing, specinfo and commandspy.
 * As well it has the class for specinfo events.
 * 
 * @author LeonTG77
 */
public class SpecManager {
	private final TeamManager teams;
	private final Main plugin;
	
	private final SpecInfo specInfo;
	
	private final ItemStack compass;
	private final ItemStack vision;
	private final ItemStack nether;
	private final ItemStack tp;
	
	/**
	 * Spectator manager class constructor.
	 * 
	 * @param plugin The main class.
	 * @param teams The team manager class.
	 */
	public SpecManager(Main plugin, TeamManager teams) {
		this.specInfo = new SpecInfo(plugin, this, teams);
		
		this.plugin = plugin;
		this.teams = teams;
		
		this.compass = new ItemStack(Material.COMPASS);
		
		ItemMeta compassMeta = compass.getItemMeta();
		compassMeta.setDisplayName(ChatColor.GREEN + "Teleporter");
		compassMeta.setLore(Arrays.asList(ChatColor.GRAY + "Left click to teleport to a random player.", ChatColor.GRAY + "Right click to open a player teleporter."));
		compass.setItemMeta(compassMeta);
		
		this.vision = new ItemStack(Material.INK_SACK, 1, (short) 12);
		
		ItemMeta visionMeta = vision.getItemMeta();
		visionMeta.setDisplayName(ChatColor.GREEN + "Toggle Night Vision");
		visionMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to toggle the night vision effect."));
		vision.setItemMeta(visionMeta);
		
		this.nether = new ItemStack(Material.LAVA_BUCKET, 1);
		
		ItemMeta netherMeta = nether.getItemMeta();
		netherMeta.setDisplayName(ChatColor.GREEN + "Players in the nether");
		netherMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to get a list of players in the nether."));
		nether.setItemMeta(netherMeta);
		
		this.tp = new ItemStack(Material.FEATHER);
		
		ItemMeta tpMeta = tp.getItemMeta();
		tpMeta.setDisplayName(ChatColor.GREEN + "Teleport to 0,0");
		tpMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to teleport to 0,0."));
		tp.setItemMeta(tpMeta);
	}
	
	private final Set<String> spectators = new HashSet<String>();
	private final Set<String> specinfo = new HashSet<String>();
	private final Set<String> cmdspies = new HashSet<String>();
	
	/**
	 * Get the instance of the specinfo class.
	 * 
	 * @return The instance.
	 */
	public SpecInfo getSpecInfo() {
		return specInfo;
	}
	
	/**
	 * Get a list of all spectators.
	 * 
	 * @return List of spectators.
	 */
	public Set<String> getSpectators() {
		return ImmutableSet.copyOf(spectators);
	}

	/**
	 * Get a list of all players with specinfo.
	 * 
	 * @return List of players with specinfo.
	 */
	public Set<String> getSpecInfoers() {
		return specinfo;
	}

	/**
	 * Get a list of all players with commandspy.
	 * 
	 * @return List of players with commandspy.
	 */
	public Set<String> getCommandSpies() {
		return cmdspies;
	}
	
	/**
	 * Enable spectator mode for the given player.
	 * 
	 * @param player the player enabling for.
	 */
	public void enableSpecmode(Player player) {
		PlayerInventory inv = player.getInventory();
		
		// remove the spectator items just to make sure they won't drop.
		inv.removeItem(compass);
		inv.removeItem(vision);
		inv.removeItem(nether);
		inv.removeItem(tp);
		
		// loop thru the players inventory contents and drop any found to the ground.
		for (ItemStack content : inv.getContents()) {
			if (content == null) {
				continue;
			}
			
			BlockUtils.dropItem(player.getLocation(), content);
		}

		// same as above just for the armor as well.
		for (ItemStack armorContent : inv.getArmorContents()) {
			if (armorContent == null || armorContent.getType() == Material.AIR) {
				continue;
			}
			
			BlockUtils.dropItem(player.getLocation(), armorContent);
		}
		
		// check if the player has any experience, if so, drop it.
		if (player.getTotalExperience() > 0) {
			ExperienceOrb exp = player.getWorld().spawn(player.getLocation(), ExperienceOrb.class);
			exp.setExperience(player.getTotalExperience());
		}
		
		User user = plugin.getUser(player);
		
		user.resetInventory();
		user.resetExp();
		user.resetEffects();
		
		player.setGameMode(GameMode.SPECTATOR);
		player.setWalkSpeed(0.2f);
		player.setFlySpeed(0.1f);
		
		player.setPlayerListName("§7§o" + player.getName());
		
		// I don't want the sidebar to have spec team names if they're on a team off.
		if (teams.getTeam(player) == null) {
			teams.joinTeam("spec", player);
		}

		spectators.add(player.getName());
		specinfo.add(player.getName());
		
		// check for permission before enabling commandspy.
		if (player.hasPermission("uhc.cmdspy") && cmdspies.contains(player.getName())) {
			cmdspies.remove(player.getName());
		}
		
		player.getInventory().setItem(1, tp);
		player.getInventory().setItem(3, compass);
		player.getInventory().setItem(5, nether);
		player.getInventory().setItem(7, vision);
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (isSpectating(online)) {
				online.showPlayer(player);
			} else {
				online.hidePlayer(player);
			}
			
			player.showPlayer(online);
		}
	}
	
	/**
	 * Disable spectator mode for the given player.
	 * 
	 * @param player the player disabling for.
	 * @param force force the disabling.
	 */
	public void disableSpecmode(Player player) {
		player.setGameMode(GameMode.SURVIVAL);
		player.setWalkSpeed(0.2f);
		player.setFlySpeed(0.1f);

		// If they're not on the spec team I won't wnat to remove them
		if (teams.getTeam(player) != null && teams.getTeam(player).getName().equals("spec")) {
			teams.leaveTeam(player, true);
		}
		
		player.setPlayerListName(null);

		spectators.remove(player.getName());
		specinfo.remove(player.getName());
		cmdspies.add(player.getName());
		
		User user = plugin.getUser(player);
		
		user.resetInventory();
		user.resetExp();
		user.resetEffects();
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (isSpectating(online)) {
				player.hidePlayer(online);
			} else {
				player.showPlayer(online);
			}
			
			online.showPlayer(player);
		}
	}
	
	/**
	 * Toggles the given player's spectator mode.
	 * 
	 * @param player the player toggling for.
	 */
	public void toggle(Player player) {
		if (isSpectating(player)) {
			enableSpecmode(player);
		} else {
			disableSpecmode(player);
		}
	}
	
	/**
	 * Check whether the given player is spectating or not.
	 * 
	 * @param player the player cheking.
	 * @return <code>true</code> if the player is speccing, <code>false</code> otherwise.
	 */
	public boolean isSpectating(Player player) {
		return isSpectating(player.getName());
	}
	
	/**
	 * Check whether the given string is in the spectator list.
	 * 
	 * @param entry the string cheking.
	 * @return <code>true</code> if the string is in the spectator list, <code>false</code> otherwise.
	 */
	public boolean isSpectating(String entry) {
		if (entry.equals("CONSOLE")) {
			return true;
		}
		
		return spectators.contains(entry);
	}

	/**
	 * Hides all the spectators for the given player.
	 * 
	 * @param player the player.
	 */
	public void hideSpectators(Player player) {
		// loop all players, hide the spectators for the player and unvanish everyone else.
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (isSpectating(online)) {
				player.hidePlayer(online);
			} else {
				player.showPlayer(online);
			}
			
			online.showPlayer(player);
		}
	}
	
	/**
	 * Check whether the given player has specinfo or not.
	 * 
	 * @param player the player cheking.
	 * @return <code>true</code> if the player has specinfo, <code>false</code> otherwise.
	 */
	public boolean hasSpecInfo(Player player) {
		return specinfo.contains(player.getName());
	}
	
	/**
	 * Check whether the given player has cmdspy or not.
	 * 
	 * @param player the player cheking.
	 * @return <code>true</code> if the player has cmdspy, <code>false</code> otherwise.
	 */
	public boolean hasCommandSpy(Player player) {
		return !cmdspies.contains(player.getName());
	}
}