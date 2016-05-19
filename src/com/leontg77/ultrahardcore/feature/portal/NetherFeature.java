package com.leontg77.ultrahardcore.feature.portal;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.utils.LocationUtils;

/**
 * Nether feature class.
 * 
 * @author LeonTG77
 */
public class NetherFeature extends ToggleableFeature implements Listener {

    public NetherFeature() {
        super("Nether", "A dimension where everything is hot and full of lava.");

        icon.setType(Material.NETHER_STALK);
        slot = 18;
    }

    private static final String WORLD_SUFFIX = "_nether";

    @EventHandler
    public void on(PlayerPortalEvent event) {
        final Player player = event.getPlayer();

        final TravelAgent travel = event.getPortalTravelAgent();
        final Location from = event.getFrom();

        if (!LocationUtils.hasBlockNearby(Material.PORTAL, from)) {
            return;
        }

        if (!isEnabled()) {
            player.sendMessage(Main.PREFIX + "The nether is disabled.");
            event.setCancelled(true);
            return;
        }
        
        final String fromName = from.getWorld().getName();
        final String targetName;
        
        switch (from.getWorld().getEnvironment()) {
        case NETHER:
            if (!fromName.endsWith(WORLD_SUFFIX)) {
                player.sendMessage(Main.PREFIX + "Could not teleport you to the overworld, contact the staff now.");
                return;
            }

            targetName = fromName.substring(0, fromName.length() - WORLD_SUFFIX.length());
            break;
        case NORMAL:
            targetName = fromName + WORLD_SUFFIX;
            break;
        default:
            return;
        }

        World world = Bukkit.getWorld(targetName);
        
        if (world == null) {
            player.sendMessage(Main.PREFIX + "The nether has not been created.");
            return;
        }

        final  double multiplier = from.getWorld().getEnvironment() == Environment.NETHER ? 8D : 0.125D;
        Location to = new Location(world, from.getX() * multiplier, from.getY(), from.getZ() * multiplier, from.getYaw(), from.getPitch());
        
        to = travel.findOrCreate(to);
        to = LocationUtils.findSafeLocationInsideBorder(to, 10, travel);
        
        if (to == null || to.getY() < 0) {
            player.sendMessage(Main.PREFIX + "Could not teleport you, contact the staff now.");
        } else {
            event.setTo(to);
        }
    }

    @EventHandler
    public void on(EntityPortalEvent event) {
        final TravelAgent travel = event.getPortalTravelAgent();
        final Location from = event.getFrom();

        if (!LocationUtils.hasBlockNearby(Material.PORTAL, from)) {
            return;
        }

        if (!isEnabled()) {
            event.setCancelled(true);
            return;
        }
        
        final String fromName = from.getWorld().getName();
        final String targetName;
        
        switch (from.getWorld().getEnvironment()) {
        case NETHER:
            if (!fromName.endsWith(WORLD_SUFFIX)) {
                return;
            }

            targetName = fromName.substring(0, fromName.length() - WORLD_SUFFIX.length());
            break;
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

        final double multiplier = from.getWorld().getEnvironment() == Environment.NETHER ? 8D : 0.125D;
        Location to = new Location(world, from.getX() * multiplier, from.getY(), from.getZ() * multiplier, from.getYaw(), from.getPitch());
        
        to = travel.findOrCreate(to);

        to = LocationUtils.findSafeLocationInsideBorder(to, 10, travel);

        if (to != null && to.getY() >= 0) {
            event.setTo(to);
        }
    }
}