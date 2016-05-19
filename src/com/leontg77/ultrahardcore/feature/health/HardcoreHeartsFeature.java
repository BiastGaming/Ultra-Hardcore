package com.leontg77.ultrahardcore.feature.health;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.protocol.HardcoreHearts;

/**
 * Hardcore Hearts feature class.
 * 
 * @author LeonTG77
 */
public class HardcoreHeartsFeature extends ToggleableFeature implements Listener {
    private final HardcoreHearts heart;
    private final Main plugin;

    public HardcoreHeartsFeature(Main plugin, HardcoreHearts heart) {
        super("Hardcore Hearts", "Players hearts are like hardcore mode hearts and it features auto respawn when someone dies.");

        icon.setType(Material.REDSTONE);
        slot = 8;

        this.plugin = plugin;
        this.heart = heart;
    }

    @Override
    public void onDisable() {
        heart.disable();
    }

    @Override
    public void onEnable() {
        heart.enable();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(final PlayerDeathEvent event) {
        if (!isEnabled()) {
            return;
        }

        final Player player = event.getEntity();

        new BukkitRunnable() {
            public void run() {
                player.spigot().respawn();
            }
        }.runTaskLater(plugin, 18);
    }
}