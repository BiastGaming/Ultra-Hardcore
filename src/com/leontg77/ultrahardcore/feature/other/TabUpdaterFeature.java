package com.leontg77.ultrahardcore.feature.other;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.utils.PacketUtils;

/**
 * Tab updater feature class.
 * 
 * @author LeonTG77
 */
public class TabUpdaterFeature extends Feature {

    /**
     * Tab updater class constructor.
     *
     * @param plugin The main class.
     * @param game The game class.
     */
    public TabUpdaterFeature(final Main plugin, final Game game) {
        super("Tab Updater", "Updates the tab list for all players.");

        new BukkitRunnable() {
            public void run() {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    PacketUtils.setTabList(online, plugin, game);
                }
            }
        }.runTaskTimer(plugin, 5, 5);
    }
}