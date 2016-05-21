package com.leontg77.ultrahardcore.scenario.scenarios.soulbrothers;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.google.common.collect.Maps;

/**
 * Player Teleporter class.
 * 
 * @author D4mnX
 */
public class PlayerTeleporter implements Listener {

    public enum Result {
        INSTANT,
        ON_NEXT_LOGIN
    }

    protected final Map<UUID, Location> scheduledLocations = Maps.newHashMap();

    /**
     * Teleport the given offline player to the given location.
     * <p>
     * Teleports on login if needed.
     * 
     * @param player The player to teleport.
     * @param loc The location to teleport to.
     * @return The result of the teleport.
     */
    public Result teleport(OfflinePlayer player, Location loc) {
        boolean isOnline = player.isOnline();

        if (isOnline) {
            player.getPlayer().teleport(loc);
        } else {
            scheduledLocations.put(player.getUniqueId(), loc);
        }

        return isOnline ? Result.INSTANT : Result.ON_NEXT_LOGIN;
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        scheduledLocations.computeIfPresent(uuid, (uuid1, location) -> {
            player.teleport(location);
            return null;
        });
    }
}