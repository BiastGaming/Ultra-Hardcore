package com.leontg77.ultrahardcore.feature.portal;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;

/**
 * Portal camping feature class.
 * 
 * @author LeonTG77
 */
public class PortalTrappingFeature extends ToggleableFeature implements Listener {

    public PortalTrappingFeature() {
        super("Portal Trapping", "People making traps to prevent others to get out of the portal.");

        icon.setType(Material.LAVA_BUCKET);
        slot = 43;
    }

    @EventHandler
    public void on(final BlockFromToEvent event) {
        if (isEnabled()) {
            return;
        }

        final Block block = event.getBlock();
        final Block to = event.getToBlock();

        if (block.getType() != Material.LAVA && block.getType() != Material.STATIONARY_LAVA) {
            return;
        }

        if (!searchForNearbyPortal(to)) {
            return;
        }

        event.setCancelled(true);
    }
    
    @EventHandler
    public void on(final PlayerBucketEmptyEvent event) {
        if (isEnabled()) {
            return;
        }

        final Block clicked = event.getBlockClicked();
        final BlockFace face = event.getBlockFace();

        final Block block = clicked.getRelative(face);
        final Player player = event.getPlayer();
        
        if (block == null) {
            return;
        }
    
        if (event.getBucket() != Material.LAVA_BUCKET) {
            return;
        }

        if (block.getType() == Material.PORTAL) {
            return;
        }

        if (!searchForNearbyPortal(block)) {
            return;
        }

        player.sendMessage(Main.PREFIX + "Portal Trapping is disallowed.");
        event.setCancelled(true);
    }

    @EventHandler
    public void on(final BlockPlaceEvent event) {
        if (isEnabled()) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        
        if (block == null) {
            return;
        }

        if (block.getType() != Material.FIRE) {
            return;
        }

        if (!searchForNearbyPortal(block)) {
            return;
        }

        player.sendMessage(Main.PREFIX + "Portal Trapping is disallowed.");
        event.setCancelled(true);
    }

    /**
     * Searches for a nearby portal block within a 5 block radius.
     *
     * @param block The block to start from.
     * @return True if there is one, false otherwise.
     */
    private boolean searchForNearbyPortal(Block block) {
        final Location loc = block.getLocation();
        int radius = 5;

        for (int x = loc.getBlockX() - radius; x <= (loc.getBlockX() + radius); x++) {
            for (int y = loc.getBlockY() - radius; y <= (loc.getBlockY() + radius); y++) {
                for (int z = loc.getBlockZ() - radius; z <= (loc.getBlockZ() + radius); z++) {
                    final Block near = loc.getWorld().getBlockAt(x, y, z);

                    if (near.getType() == Material.PORTAL) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}