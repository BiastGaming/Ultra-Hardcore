package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * Balance scenario class
 * 
 * @author LeonTG77
 */
public class Balance extends Scenario implements Listener {
	private static final String PREFIX = "§3Balance §8» §7";
	
	private final Game game;
	
	public Balance(Game game) {
		super("Balance", "After the 5th diamond, it gets progressively harder to obtain diamonds.");
		
		this.game = game;
	}
	
	private final Map<String, Integer> minedAmount = new HashMap<String, Integer>();
	private final Map<String, Integer> chance = new HashMap<String, Integer>();

	@Override
	public void onDisable() {
		minedAmount.clear();
		chance.clear();
	}

	@Override
	public void onEnable() {
		onDisable();
	}
	
	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		final Player player = event.getPlayer();
		final Block block = event.getBlock();
		
		if (!game.getWorlds().contains(block.getWorld())) {
			return;
		}
		
		if (block.getType() != Material.DIAMOND_ORE) {
			return;
		}
		
		if (!minedAmount.containsKey(player.getName())) {
			minedAmount.put(player.getName(), 0);
		}
		
		int amount = minedAmount.get(player.getName());
		amount++;
		
		minedAmount.put(player.getName(), amount);
		
		if (amount < 5) {
			return;
		}
		
		if (amount == 5) {
			player.sendMessage(PREFIX + "Uh oh! Diamonds might disappear when mined now!");
			return;
		}
		
		chance.put(player.getName(), amount - 3);
		
		final int rand = new Random().nextInt(chance.get(player.getName()));
		
		if (rand == 1) {
			chance.put(player.getName(), chance.get(player.getName()) + 1);
			
			player.sendMessage(PREFIX + "Diamonds now have a §a1/" + chance.get(player.getName()) + " §7chance to drop!");
		} else {
			BlockUtils.blockBreak(player, block);
			BlockUtils.degradeDurabiliy(player);
			
			event.setCancelled(true);
			block.setType(Material.AIR);
		}
    }
}