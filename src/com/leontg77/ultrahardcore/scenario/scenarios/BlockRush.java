 package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * BlockRush scenario class
 * 
 * @author LeonTG77
 */
public class BlockRush extends Scenario implements Listener {
	private final Set<String> minedBlocks = new HashSet<String>();
	
	private static final ItemStack ITEM_TO_DROP = new ItemStack(Material.GOLD_INGOT);
	private static final String PREFIX = "§6§lBlockRush §8» §e";
	
	public BlockRush() {
		super("BlockRush", "Mining a specific block type for the first time drops 1 gold ingot.");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void on(final BlockBreakEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		final Player player = event.getPlayer();
		final Block block = event.getBlock();
		
		final String name = block.getState().getData().toString();
		
		if (minedBlocks.contains(name)) {
			return;
		}
		
		PlayerUtils.broadcast(PREFIX + player.getName() + "§7 was the first to break §e" + name.toLowerCase().replaceAll("_", " ") + "§7.");
		minedBlocks.add(name);
		
		BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), ITEM_TO_DROP);
	}
}