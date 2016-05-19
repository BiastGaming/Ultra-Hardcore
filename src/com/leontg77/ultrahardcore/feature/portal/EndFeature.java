package com.leontg77.ultrahardcore.feature.portal;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.utils.LocationUtils;

/**
 * End feature class.
 * 
 * @author LeonTG77
 */
public class EndFeature extends ToggleableFeature implements Listener {
    private final Main plugin;
    private final Game game;

    public EndFeature(Main plugin, Game game) {
        super("The End", "A dimension with a floating island where the dragon lives.");

        icon.setType(Material.ENDER_PORTAL_FRAME);
        slot = 19;

        this.plugin = plugin;
        this.game = game;
    }

    private static final String WORLD_SUFFIX = "_end";

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        final Player player = event.getPlayer();
        final Location from = event.getFrom();

        if (!LocationUtils.hasBlockNearby(Material.ENDER_PORTAL, from)) {
            return;
        }

        if (!isEnabled()) {
            player.sendMessage(Main.PREFIX + "The end is disabled.");
            event.setCancelled(true);
            return;
        }
        
        String fromName = from.getWorld().getName();
        String targetName;
        
        switch (from.getWorld().getEnvironment()) {
        case THE_END:
            if (game.getWorld() == null) {
                event.setTo(plugin.getSpawn());
            } else {
                event.setTo(game.getWorld().getSpawnLocation());
            }
            return;
        case NORMAL:
            targetName = fromName + WORLD_SUFFIX;
            break;
        default:
            return;
        }

        final World world = Bukkit.getWorld(targetName);
        
        if (world == null) {
            player.sendMessage(Main.PREFIX + "The end has not been created.");
            return;
        }

        final Location to = new Location(world, 100.0, 49, 0, 90f, 0f);
        generateEndPlatform(world, to);

        event.setTo(to);
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        final Location from = event.getFrom();

        if (!LocationUtils.hasBlockNearby(Material.ENDER_PORTAL, from)) {
            return;
        }

        if (!isEnabled()) {
            event.setCancelled(true);
            return;
        }
        
        final String fromName = from.getWorld().getName();
        final String targetName;
        
        switch (from.getWorld().getEnvironment()) {
        case THE_END:
            if (game.getWorld() == null) {
                event.getEntity().remove();
            } else {
                event.setTo(game.getWorld().getSpawnLocation());
            }
            return;
        case NORMAL:
            targetName = fromName + WORLD_SUFFIX;
            break;
        default:
            return;
        }

        final World world = Bukkit.getWorld(targetName);
        
        if (world == null) {
            return;
        }

        final Location to = new Location(world, 100.0, 49, 0, 90f, 0f);
        generateEndPlatform(world, to);

        event.setTo(to);
    }
    
    /**
     * Generate the obsidian end spawn platform.
     * 
     * @param world The world to create it in.
     * @param loc The location to create it at.
     */
    private void generateEndPlatform(World world, Location loc) {
        for (int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 2; y++) {
            for (int x = loc.getBlockX() - 2; x <= loc.getBlockX() + 2; x++) {
                for (int z = loc.getBlockZ() - 2; z <= loc.getBlockZ() + 2; z++) {
                    Block block = world.getBlockAt(x, y, z);

                    if (y == 48) {
                        block.setType(Material.OBSIDIAN);
                        block.getState().update();
                    } else {
                        block.setType(Material.AIR);
                        block.getState().update();
                    }
                }
            }
        }
    }
}