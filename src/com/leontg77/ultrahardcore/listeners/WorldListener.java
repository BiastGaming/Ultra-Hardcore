package com.leontg77.ultrahardcore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.leontg77.pregenner.events.WorldBorderFillFinishedEvent;
import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * World listener class.
 * <p>
 * Contains all eventhandlers for world releated events.
 * 
 * @author LeonTG77
 */
public class WorldListener implements Listener {
	private final Arena arena;

	/**
	 * World listener class constructor.
	 *
	 * @param arena The arena class.
	 */
	public WorldListener(Arena arena) {
		this.arena = arena;
	}

	@EventHandler
	public void on(ChunkUnloadEvent event) {
		if (!State.isState(State.SCATTER)) {
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler
	public void on(WorldBorderFillFinishedEvent event) {
		World world = event.getWorld();

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload " + world.getName() + " clear");

		if (arena.isResetting) {
			PlayerUtils.broadcast(Arena.PREFIX + "Arena reset complete.");

			if (arena.wasEnabled) {
				arena.enable();
				PlayerUtils.broadcast(Arena.PREFIX + "The arena has been enabled.");
				PlayerUtils.broadcast(Arena.PREFIX + "You can use §a/a §7to join it.");
			}

			arena.wasEnabled = false;
			arena.isResetting = false;
			return;
		}

		PlayerUtils.broadcast(Main.PREFIX + "Pregen of world '§a" + world.getName() + "§7' finished.");
	}
}