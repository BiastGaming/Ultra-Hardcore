package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Paranoia scenario class
 * 
 * @author LeonTG77
 */
public class Paranoia extends Scenario implements Listener {
	public static final String PREFIX = "�c�lParanoia �8� �f";
	
	public Paranoia() {
		super("Paranoia", "Your coordinates are broadcasted when you mine diamonds/gold, craft or eat an golden apple, you craft an anvil or enchantment table or you die");
	}

	@Override
	public void onDisable() {
		BoardManager.getInstance().setup();
	}

	@Override
	public void onEnable() {
		Scoreboard board = BoardManager.getInstance().getBoard();
		
		board.clearSlot(DisplaySlot.PLAYER_LIST);
		board.clearSlot(DisplaySlot.BELOW_NAME);
	}
	
	@EventHandler
	public void on(BlockBreakEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		Location loc = player.getLocation();
		
		if (block.getType() == Material.DIAMOND_ORE) {
			PlayerUtils.broadcast(PREFIX + ChatColor.GREEN + player.getName() + "�7 mined �bdiamond ore �7at " + location(loc));
		}
		
		if (block.getType() == Material.GOLD_ORE) {
			PlayerUtils.broadcast(PREFIX + ChatColor.GREEN + player.getName() + "�7 mined �6gold ore �7at " + location(loc));
		}
	}
	
	@EventHandler
	public void on(PlayerItemConsumeEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		Player player = event.getPlayer();
		Location loc = player.getLocation();
		
		if (event.getItem().getType() == Material.GOLDEN_APPLE) {
			PlayerUtils.broadcast(PREFIX + ChatColor.GREEN + player.getName() + "�7 ate a �eGolden Apple �7at " + location(loc));
		}
	}
	
	@EventHandler
	public void on(CraftItemEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		HumanEntity player = event.getWhoClicked();
		Location loc = player.getLocation();
		
		if (event.getRecipe().getResult().getType() == Material.GOLDEN_APPLE) {
			PlayerUtils.broadcast(PREFIX + ChatColor.GREEN + player.getName() + "�7 crafted a �eGolden Apple �7at " + location(loc));
		}
		
		if (event.getRecipe().getResult().getType() == Material.ANVIL) {
			PlayerUtils.broadcast(PREFIX + ChatColor.GREEN + player.getName() + "�7 crafted an �dAnvil �7at " + location(loc));
		}
		
		if (event.getRecipe().getResult().getType() == Material.ENCHANTMENT_TABLE) {
			PlayerUtils.broadcast(PREFIX + ChatColor.GREEN + player.getName() + "�7 crafted an �5Enchantment Table �7at " + location(loc));
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void on(PlayerDeathEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		Player player = event.getEntity();
		Location loc = player.getLocation();

		PlayerUtils.broadcast(PREFIX + ChatColor.GREEN + player.getName() + "�7 died at " + location(loc));
	}

	/**
	 * Get the given location in string form.
	 * 
	 * @param loc the given location.
	 * @return Location in String form.
	 */
	private String location(Location loc) {
		return "x:" + loc.getBlockX() + " y:" + loc.getBlockY() + " z:" + loc.getBlockZ();
	}
}