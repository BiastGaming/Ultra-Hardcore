package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.FinalHealEvent;
import com.leontg77.ultrahardcore.events.PvPEnableEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Skyhigh scenario class
 * 
 * @author LeonTG77
 */
public class Skyhigh extends Scenario implements Listener {
	public static final String PREFIX = "§bSkyhigh §8§ §7";
	
	private final Main plugin;
	private final Game game;
	
	public Skyhigh(Main plugin, Game game) {
		super("Skyhigh", "After 45 minutes, any player below y: 101 will begin to take half a heart of damage every 30 seconds.");
		
		this.plugin = plugin;
		this.game = game;
	}

	private BukkitRunnable task = null;
	
	@Override
	public void onDisable() {
		if (task != null && Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId())) {
			task.cancel();
		}
		
		task = null;
	}

	@Override
	public void onEnable() {}
	
	@EventHandler
	public void on(FinalHealEvent event) {
		for (Player online : game.getPlayers()) {
			PlayerUtils.giveItem(online, new ItemStack(Material.DIAMOND_SPADE, 1));
			PlayerUtils.giveItem(online, new ItemStack(Material.FEATHER, 32));
			PlayerUtils.giveItem(online, new ItemStack(Material.STRING, 2));
			PlayerUtils.giveItem(online, new ItemStack(Material.PUMPKIN, 3));
			PlayerUtils.giveItem(online, new ItemStack(Material.STAINED_CLAY, 64, (short) 14));
			PlayerUtils.giveItem(online, new ItemStack(Material.STAINED_CLAY, 64, (short) 14));
			PlayerUtils.giveItem(online, new ItemStack(Material.SNOW_BLOCK, 64));
		}
		
		PlayerUtils.broadcast(PREFIX + "Skyhigh items have been given.");
	}
	
	@EventHandler
	public void on(PvPEnableEvent event) {
		task = new BukkitRunnable() {
			public void run() {
				for (Player online : game.getPlayers()) {
					if (online.getLocation().getY() > 101) {
						continue;
					}
					
					online.sendMessage(PREFIX + "Ouch, You need to be above y:101 in skyhigh.");
					PlayerUtils.damage(online, 1);
				}
			}
		};
		
		task.runTaskTimer(plugin, 600, 600);
	}
}