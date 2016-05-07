package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Blocked scenario class
 * 
 * @author LeonTG77
 */
public class Blocked extends Scenario implements Listener {
	private Map<String, List<Location>> mined = new HashMap<String, List<Location>>();
    public static final String PREFIX = "§9§lBlocked §8» §7";
	
	public Blocked() {
		super("Blocked", "You can't break the blocks that you place. Other players can break blocks that you place, and you can break blocks that other players place.");
	}
	
	@Override
	public void onDisable() {
		mined.clear();
	}

	@Override
	public void onEnable() {
		mined.clear();
	}
	
	@EventHandler
	public void on(BlockPlaceEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		final Player player = event.getPlayer();
		final Block block = event.getBlock();
		
		if (!mined.containsKey(player.getName())) {
			mined.put(player.getName(), new ArrayList<Location>());
		}
		
		mined.get(player.getName()).add(block.getLocation());
	}
	
	@EventHandler
	public void on(BlockBreakEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		final Player player = event.getPlayer();
		final Block block = event.getBlock();
		
		if (!mined.containsKey(player.getName())) {
			mined.put(player.getName(), new ArrayList<Location>());
		}
		
		if (mined.get(player.getName()).contains(block.getLocation())) {
			player.sendMessage(PREFIX + "You can't break blocks you placed, silly dilly.");
			event.setCancelled(true);
		}
	}
}