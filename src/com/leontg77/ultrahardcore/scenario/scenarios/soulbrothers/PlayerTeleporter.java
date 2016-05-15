package com.leontg77.ultrahardcore.scenario.scenarios.soulbrothers;

import com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.UUID;

public class PlayerTeleporter implements Listener {

    public enum Result {
        INSTANT,
        ON_NEXT_LOGIN
    }

    protected final Map<UUID, Location> scheduledLocations = Maps.newHashMap();

    public Result teleport(OfflinePlayer player, Location location) {
        boolean isOnline = player.isOnline();

        if (isOnline) {
            player.getPlayer().teleport(location);
        } else {
            scheduledLocations.put(player.getUniqueId(), location);
        }

        return isOnline ? Result.INSTANT : Result.ON_NEXT_LOGIN;
    }

    @EventHandler
    protected void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        scheduledLocations.computeIfPresent(uuid, (uuid1, location) -> {
            player.teleport(location);
            return null;
        });
    }
}