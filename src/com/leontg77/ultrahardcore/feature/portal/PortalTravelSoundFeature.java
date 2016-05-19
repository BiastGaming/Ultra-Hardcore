package com.leontg77.ultrahardcore.feature.portal;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Portal Travel sound feature class.
 * 
 * @author LeonTG77
 */
public class PortalTravelSoundFeature extends Feature implements Listener {
    private final Main plugin;

    public PortalTravelSoundFeature(Main plugin) {
        super("Portal Travel Sound", "Play a sound when a player uses a portal.");

        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(final PlayerPortalEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();

        new BukkitRunnable() {
            public void run() {
                player.playSound(event.getTo(), Sound.PORTAL_TRAVEL, 0.3f, 1);
            }
        }.runTask(plugin);
    }
}