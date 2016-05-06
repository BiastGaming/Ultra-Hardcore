package com.leontg77.ultrahardcore.feature.border;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Runnable for the border shrinking.
 * 
 * @author LeonTG77
 */
public class BorderRunnable extends BukkitRunnable {
	private final Game game;
	private int next;
	
	protected BorderRunnable(Game game, int next) {
		this.game = game;
		this.next = next;
	}
	
	@Override
	public void run() {
		next--;

		int borderSize = 0;

		for (World world : game.getWorlds()) {
			borderSize = (int) world.getWorldBorder().getSize();
		}
		
		switch (next) {
		case 600:
		case 300:
		case 60:
		case 30:
		case 10:
		case 5:
		case 4:
		case 3:
		case 2:
		case 1:
			PlayerUtils.broadcast(Main.BORDER_PREFIX + "The border starts shrinking in §a" + DateUtils.advancedTicksToString(next) + "§7.");
			break;
		case 0:
			int size = 300;
			
			switch (borderSize) {
			case 300:
				size = 200;
				break;
			case 200:
				size = 100;
				break;
			}
				
			PlayerUtils.broadcast(Main.BORDER_PREFIX + "Border will now shrink to §6" + size + "x" + size + " §7over §a10 §7minutes.");
			
			for (Player online : Bukkit.getOnlinePlayers()) {
				online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
			}

			for (World world : game.getWorlds()) {
				world.getWorldBorder().setSize(size, 600);
			}
			break;
		case -600:
			for (Player online : Bukkit.getOnlinePlayers()) {
				online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
			}
			
			PlayerUtils.broadcast(Main.BORDER_PREFIX + "Border has stopped shrinking.");
			
			if (borderSize == 100) {
				cancel();
				return;
			}
			
			PlayerUtils.broadcast(Main.BORDER_PREFIX + "Next shrink is in §a15§7 minutes.");
			
			next = 900;
			break;
		}
	}
}