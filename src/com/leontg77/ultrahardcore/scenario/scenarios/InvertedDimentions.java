package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * InvertedDimentions scenario class.
 * 
 * @author LeonTG77
 */
public class InvertedDimentions extends Scenario implements CommandExecutor, Listener {
	public static final String PREFIX = "§cInverted Dimentions §8» §7";
	
	private final Main plugin;
	
	/**
	 * InvertedDimentions class constructor.
	 * 
	 * @param plugin The main class.
	 */
	public InvertedDimentions(Main plugin) {
		super("InvertedDimentions", "The overworld surface is how nether would look like and vice versa. Leaves drop sugar canes.");
		
		this.plugin = plugin;
		
		plugin.getCommand("invert").setExecutor(this);
	}
	
	private final List<Location> locs = new ArrayList<Location>();
	
	private BukkitRunnable task = null;
	private double totalChunks = 0;
	
	@Override
	public void onDisable() {
		if (task != null) {
			PlayerUtils.broadcast(PREFIX + "Cancelling inverted dimentions generation.");
			task.cancel();
		}
		
		totalChunks = 0;
		task = null;
	}

	@EventHandler
    public void on(BlockFromToEvent event) {
		if (task == null) {
			return;
		}
		
        event.setCancelled(true);
    }

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		IgniteCause cause = event.getCause();

		if (cause == IgniteCause.LAVA) {
			event.setCancelled(true);
			return;
		}

		if (cause == IgniteCause.SPREAD) {
			event.setCancelled(true);
		}
	}
	
	private final Random rand = new Random();

	@EventHandler
	public void on(LeavesDecayEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		Block block = event.getBlock();
		Location loc = block.getLocation().add(0.5, 0.7, 0.5);
		
		if (rand.nextInt(100) < 2) {
			BlockUtils.dropItem(loc, new ItemStack(Material.SUGAR_CANE, 1 + rand.nextInt(1)));
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!isEnabled()) {
			sender.sendMessage(PREFIX + "Inverted Dimentions is not enabled.");
			return true;
		}
		
		if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
			sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			return true;
		}
		
		if (args.length < 2) {
			sender.sendMessage(PREFIX + "Usage: /invert <world> <diameter>");
			return true;
		}
		
		final World world = Bukkit.getWorld(args[0]);
		
		if (world == null) {
			sender.sendMessage(ChatColor.RED + "The world '" + args[0] + "' does not exist.");
			return true;
		}
		
		int diameter;
		
		try {
			diameter = parseInt(args[1], "diameter") / 2;
		} catch (Exception ex) {
			sender.sendMessage(ChatColor.RED + ex.getMessage());
			return true;
		}
		
		locs.clear();
		
		for (int x = -1 * diameter; x < diameter; x += 16) {
			for (int z = -1 * diameter; z < diameter; z += 16) {
				locs.add(new Location(world, x, 1, z));
			}
		}
		
		totalChunks = locs.size();

		PlayerUtils.broadcast(PREFIX + "Starting inverted dimentions generation in world '§a" + world.getName() + "§7'.");
		
		task = new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				if (locs.size() == 0) {
					PlayerUtils.broadcast(PREFIX + "The inverted dimentions generation has finished.");
					
					cancel();
					task = null;
					return;
				}

				Location loc = locs.remove(locs.size() - 1);
				Chunk chunk = world.getChunkAt(loc);
				
				for (int y = 128; y >= 0; y--) {
					for (int x = 0; x < 16; x++) {
						for (int z = 0; z < 16; z++) {
							final Block block = chunk.getBlock(x, y, z);

							switch (block.getType()) {
							case FIRE:
								block.setType(Material.LONG_GRASS);
								block.setData((byte) 1);
								break;
							case LONG_GRASS:
								if (rand.nextInt(8) == 0) {
									block.setType(Material.FIRE);
								}
								break;
							case GRASS:
							case DIRT:
								block.setType(Material.NETHERRACK);
								break;
							case NETHERRACK:
								if (block.getRelative(BlockFace.UP).isEmpty() || !block.getRelative(BlockFace.UP).getType().isSolid()) {
									block.setType(Material.GRASS);
								} else {
									block.setType(Material.DIRT);
								}
								break;
							case SAND:
								block.setType(Material.SOUL_SAND);
								break;
							case SOUL_SAND:
								block.setType(Material.SAND);
								break;
							case STATIONARY_LAVA:
							case LAVA:
								block.setType(Material.BARRIER);
								
								new BukkitRunnable() {
									public void run() {
										block.setType(Material.WATER);
									}
								}.runTaskLater(plugin, 100);
								break;
							case STATIONARY_WATER:
							case WATER:
							case ICE:
								block.setType(Material.OBSIDIAN);
								
								new BukkitRunnable() {
									public void run() {
										block.setType(Material.LAVA);
									}
								}.runTaskLater(plugin, 100);
								break;
							default:
								break;
							}
						}
					}
				}

				double completed = ((totalChunks - ((double) locs.size())) * 100 / totalChunks);
				
				for (Player online : Bukkit.getOnlinePlayers()) {
					PacketUtils.sendAction(online, PREFIX + "Generating inverted dimentions §8(§a" + NumberUtils.formatPercentDouble(completed) + "%§8)");
				}
			}
		};
		
		task.runTaskTimer(plugin, 1, 1);
		return true;
	}
}