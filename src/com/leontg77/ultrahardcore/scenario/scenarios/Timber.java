package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * Timber scenario class
 * 
 * @author LeonTG77
 */
public class Timber extends Scenario implements Listener {
	
	public Timber() {
		super("Timber", "When chopping down a tree the entire tree will chop.");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}
	
	@EventHandler
	public void on(BlockBreakEvent event) {
		Block block = event.getBlock();
		
		if (block.getType() != Material.LOG && block.getType() != Material.LOG_2) {
			return;
		}

		List<Block> vein = new ArrayList<Block>();
		BlockUtils.getVein(block, vein);
		
		for (Block ore : vein) {	
			BlockUtils.blockBreak(null, ore);
			ore.breakNaturally();
		}
	}
}