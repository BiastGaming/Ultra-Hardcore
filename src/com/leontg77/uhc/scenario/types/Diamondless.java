package com.leontg77.uhc.scenario.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.utils.BlockUtils;

/**
 * Diamondless scenario class
 * 
 * @author LeonTG77
 */
public class Diamondless extends Scenario implements Listener {
	private boolean enabled = false;

	public Diamondless() {
		super("Diamondless", "You can't obtain diamonds");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
		if (!isEnabled()) {
			return;
		}
		
    	Player player = event.getPlayer();
		Block block = event.getBlock();
    	
		boolean cutclean = ScenarioManager.getInstance().getScenario("CutClean").isEnabled();
	    	
		if (block.getType() == Material.DIAMOND_ORE) {
	    	event.setCancelled(true);
	    	BlockUtils.blockCrack(player, block.getLocation(), block.getType());
			block.setType(Material.AIR);
			block.getState().update();
			Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (cutclean ? Material.IRON_INGOT : Material.IRON_ORE));
			item.setVelocity(new Vector(0, 0.2, 0));
		}
    }
}