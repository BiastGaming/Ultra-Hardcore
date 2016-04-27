package com.leontg77.ultrahardcore.feature.other;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Spectator Location Fix feature class.
 * 
 * @author LeonTG77
 */
public class SpecLocationFixFeature extends Feature {

	/**
	 * Spectator Location Fix class constructor.
	 * 
	 * @param plugin The main class.
	 */
	public SpecLocationFixFeature(Main plugin) {
		super("Spectator Location Fix", "Makes sure spectators are in the right location when they have a spectator target.");
		
		new BukkitRunnable() {
			public void run() {
				for (Player online : Bukkit.getOnlinePlayers()) {
					if (online.getGameMode() != GameMode.SPECTATOR) {
						continue;
					}
					
					Entity target = online.getSpectatorTarget();
					
					if (target != null) {
						online.teleport(target);
					}
				}
			}
		}.runTaskTimer(plugin, 1, 1);
	}
}