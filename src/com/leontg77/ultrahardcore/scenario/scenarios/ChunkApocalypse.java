package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * ChunkApocalypse scenario class
 * 
 * @author LeonTG77
 */
public class ChunkApocalypse extends Scenario implements Listener, CommandExecutor {
	public static final String PREFIX = "§b[§2ChunkApoc§b] §3";
	
	private final Main plugin;
	
	public ChunkApocalypse(Main plugin) {
		super("ChunkApocalypse", "Every chunk has a 30% chance of being replaced with air");
		
		plugin.getCommand("chunkapo").setExecutor(this);
		
		this.plugin = plugin;
	}
	
	private ArrayList<Location> locations = new ArrayList<Location>();
	private int totalChunks;
	
	private BukkitRunnable task = null;

	@EventHandler
    public void on(BlockFromToEvent event) {
		if (task == null) {
			return;
		}
		
        event.setCancelled(true);
    }
	
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can generate chunk apocalypse.");
			return true;
		}
		
		final Player player = (Player) sender;
		
		if (!isEnabled()) {
			sender.sendMessage(PREFIX + "ChunkApocalypse is not enabled.");
			return true;
		}
		
		if (!sender.hasPermission("uhc.chunkapo")) {
			sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			return true;
		}
		
		if (args.length == 0) {
			player.sendMessage(PREFIX + "Usage: /chunkapo <radius>");
			return true;
		}
		
		int radius;
		
		try {
			radius = Integer.parseInt(args[0]);
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED + args[0] + " is not an valid radius.");
			return true;
		}
		
		locations = new ArrayList<Location>();
		
		for (int x = -1 * radius; x < radius; x += 16) {
			for (int z = -1 * radius; z < radius; z += 16) {
				if (new Random().nextInt(99) < 30) {
					locations.add(new Location(player.getWorld(), x, 1, z));
				}
			}
		}
		
		totalChunks = locations.size();

		PlayerUtils.broadcast(PREFIX + "ChunkApocalypse generation started.");
		
		task = new BukkitRunnable() {
			public void run() {
				if (locations.size() == 0) {
					PlayerUtils.broadcast(PREFIX + "ChunkApocalypse generation finished.");
					cancel();
					task = null;
					return;
				}

				Location loc = locations.remove(locations.size() - 1);
				Chunk chunk = player.getWorld().getChunkAt(loc);
				
				for (int y = 0; y < 128; y++) {
					for (int x = 0; x < 16; x++) {
						for (int z = 0; z < 17; z++) {
							Block block = chunk.getBlock(x, y, z);
							
							block.setType(Material.AIR);
						}
					}
				}

				int percentCompleted = ((totalChunks - locations.size())*100 / totalChunks);
				
				for (Player online : Bukkit.getOnlinePlayers()) {
					PacketUtils.sendAction(online, PREFIX + "Removed chunk at x:" + chunk.getX() + " z:" + chunk.getZ() + ", §6" + percentCompleted + "% §7finished");
				}
			}
		};
		
		task.runTaskTimer(plugin, 1, 1);
		return true;
	}
}