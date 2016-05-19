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
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * Flint rates feature class.
 * 
 * @author LeonTG77
 */
public class FlintRatesFeature extends Feature implements Listener {
    private static final String RATE_PATH = "rates.flint.rate";

    private final Settings settings;
    private double flintRate;

    public FlintRatesFeature(Settings settings) {
        super("Flint rates", "Modifies the vanilla flint rates to a higher rate.");

        flintRate = settings.getConfig().getDouble(RATE_PATH, 0.0055);

        this.settings = settings;
    }

    /**
     * Set the rate of flint to drop.
     *
     * @param rate The new rate.
     */
    public void setFlintRates(final double rate) {
        this.flintRate = (rate / 100);
        
        settings.getConfig().set(RATE_PATH, flintRate);
        settings.saveConfig();
    }

    /**
     * Get the current flint rates.
     * 
     * @return The flint rates.
     */
    public double getFlintRate() {
        return flintRate;
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        // no drops if they're in creative.
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        final Random rand = new Random();

        if (block.getType() != Material.GRAVEL) {
            return;
        }

        final Location toDrop = block.getLocation().add(0.5, 0.7, 0.5);

        BlockUtils.blockBreak(player, block);
        BlockUtils.degradeDurabiliy(player);

        event.setCancelled(true);
        block.setType(Material.AIR);
        
        if (rand.nextDouble() < flintRate) {
            BlockUtils.dropItem(toDrop, new ItemStack(Material.FLINT));
        } else {
            BlockUtils.dropItem(toDrop, new ItemStack(Material.GRAVEL));
        }
    }
}