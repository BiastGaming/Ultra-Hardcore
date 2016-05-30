package com.leontg77.ultrahardcore.feature.rates;

import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.utils.BlockUtils;
import com.leontg77.ultrahardcore.utils.TreeUtils;
import com.leontg77.ultrahardcore.utils.TreeUtils.TreeType;

/**
 * Apple rates feature class.
 * 
 * @author LeonTG77
 */
public class AppleRatesFeature extends Feature implements Listener {
    private static final String RATE_PATH = "rates.apple.rate";
    private static final double SAPLING_RATE = 0.05;

    private final Settings settings;
    private double appleRate;

    public AppleRatesFeature(Settings settings) {
        super("Apple rates", "Modifies the vanilla apple rates to a higher rate.");

        appleRate = settings.getConfig().getDouble(RATE_PATH, 0.0055);

        this.settings = settings;
    }

    /**
     * Set the rate of apples to drop.
     *
     * @param rate The new rate.
     */
    public void setAppleRates(final double rate) {
        this.appleRate = (rate / 100);
        
        settings.getConfig().set(RATE_PATH, appleRate);
        settings.saveConfig();
    }

    /**
     * Get the current apple rates.
     * 
     * @return The apple rates.
     */
    public double getAppleRates() {
        return appleRate;
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        // breaking leaves in creative shouldn't drop anything...
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (block.getType() != Material.LEAVES && block.getType() != Material.LEAVES_2) {
            return;
        }

        // this is just so I can cancel it
        // if it returns true it means I should cancel it and set the block to air.
        if (!handleLeafBreak(event)) {
            return;
        }

        BlockUtils.blockBreak(player, block);
        BlockUtils.degradeDurabiliy(player);

        event.setCancelled(true);
        block.setType(Material.AIR);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeavesDecay(LeavesDecayEvent event) {
        final Block block = event.getBlock();

        if (block.getType() != Material.LEAVES && block.getType() != Material.LEAVES_2) {
            return;
        }

        // this is just so I can cancel it
        // if it returns true it means I should cancel it and set the block to air.
        if (!handleLeafBreak(event)) {
            return;
        }

        event.setCancelled(true);
        block.setType(Material.AIR);
    }

    /**
     * Handle a leaf break event, wether it's from decaying or a player breaking it.
     *
     * @param event The LeafDecayEvent or BlockBreakEvent that should trigger this.
     * @return True if the event that called this should be cancelled, false otherwise.
     */
    private boolean handleLeafBreak(BlockEvent event) {
        final Block block = event.getBlock();
        final int damage = BlockUtils.getDurability(block);

        final TreeType tree = TreeUtils.getTree(block.getType(), damage);
        final Random rand = new Random();

        if (tree == TreeType.UNKNOWN) {
            return false;
        }

        final Location toDrop = block.getLocation().add(0.5, 0.7, 0.5);

        if (rand.nextDouble() < SAPLING_RATE) {
            BlockUtils.dropItem(toDrop, tree.getSapling());
        }

        if (tree != TreeType.OAK && tree != TreeType.DARK_OAK) {
            return true;
        }

        if (rand.nextDouble() >= appleRate) {
            return true;
        }

        BlockUtils.dropItem(toDrop, new ItemStack(Material.APPLE, 1));
        return true;
    }
}