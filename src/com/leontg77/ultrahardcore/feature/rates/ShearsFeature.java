package com.leontg77.ultrahardcore.feature.rates;

import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.utils.BlockUtils;
import com.leontg77.ultrahardcore.utils.TreeUtils;
import com.leontg77.ultrahardcore.utils.TreeUtils.TreeType;

/**
 * Shears feature class.
 * 
 * @author LeonTG77
 */
public class ShearsFeature extends ToggleableFeature implements Listener {
	private static final String RATE_PATH = "feature.shears.rate";
    private double shearRate;

	public ShearsFeature() {
		super("Shears", "Using shears on leaves gives an increased rate of apples.");
		
		icon.setType(Material.SHEARS);
		slot = 3;
	}

	/**
	 * Set the rate of shears to drop apples.
	 * 
	 * @param rate The new rate.
	 */
    public void setShearRates(final double rate) {
    	this.shearRate = (rate / 100);
        
        settings.getConfig().set(RATE_PATH, shearRate);
        settings.saveConfig();
    }

    /**
     * Get the current shear rates for apples.
     * 
     * @return The shear rates for apples.
     */
    public double getShearRates() {
        return shearRate;
    }
	
	@EventHandler(priority = EventPriority.LOW)
    public void on(BlockBreakEvent event) {
		if (!isEnabled()) {
			return;
		}
		
    	final Player player = event.getPlayer();
		final Block block = event.getBlock();
    	
		// breaking leaves in creative shouldn't drop anything...
		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		if (block.getType() != Material.LEAVES && block.getType() != Material.LEAVES_2) {
			return;
		}
		
		final int damage = BlockUtils.getDurability(block);
		
		final TreeType tree = TreeUtils.getTree(block.getType(), damage);
		final Random rand = new Random();
		
		if (tree == TreeType.UNKNOWN) {
			return;
		}
		
		final Location toDrop = block.getLocation().add(0.5, 0.7, 0.5);
		final ItemStack item = player.getItemInHand();
		
		if (tree != TreeType.OAK && tree != TreeType.DARK_OAK) {
			return;
		}
		
		if (item == null || item.getType() != Material.SHEARS) {
			return;
		}

		BlockUtils.dropItem(toDrop, tree.getLeaf());
		
		BlockUtils.blockBreak(player, block);
		BlockUtils.degradeDurabiliy(player);
		
		event.setCancelled(true);
		block.setType(Material.AIR);
		
		if (rand.nextDouble() >= shearRate) {
			return;
		}
		
		BlockUtils.dropItem(toDrop, new ItemStack(Material.APPLE, 1));
    }
}