package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Flooded scenario class.
 * 
 * @author LeonTG77
 */
public class Flooded extends Scenario implements CommandExecutor, Listener {
	public static final String PREFIX = "§9Flooded §8» §7";

	private final Main plugin;
	private final Game game;
	
	/**
	 * Flooded class constructor.
	 * 
	 * @param plugin The main class.
	 * @param game The game class.
	 */
	public Flooded(Main plugin, Game game) {
		super("Flooded", "All air blocks between y32 -> y70 are filled with water. Everyone gets permanent water breathing & night vision, and start with a depth strider 3 book, some sugar cane, and spawn eggs for a bunch of useful animals. It will also have permanent rain.");

		this.plugin = plugin;
		this.game = game;
		
		plugin.getCommand("flood").setExecutor(this);
	}
	
	private final List<Location> locs = new ArrayList<Location>();
	
	private BukkitRunnable task = null;
	private double totalChunks = 0;
	
	@Override
	public void onDisable() {
		if (task != null) {
			PlayerUtils.broadcast(PREFIX + "Cancelling flooded generation.");
			task.cancel();
		}
		
		totalChunks = 0;
		task = null;
	}
	
	@Override
	public void onEnable() {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		on(new GameStartEvent());
	}

	@EventHandler
    public void on(GameStartEvent event) {
		PotionEffect vision = new PotionEffect(PotionEffectType.NIGHT_VISION, NumberUtils.TICKS_IN_999_DAYS, 0);
		PotionEffect water = new PotionEffect(PotionEffectType.WATER_BREATHING, NumberUtils.TICKS_IN_999_DAYS, 0);
		
		ItemStack chicken = new ItemStack(Material.MONSTER_EGG, 12, (short) 93);
		ItemStack cow = new ItemStack(Material.MONSTER_EGG, 8, (short) 92);
		
		ItemStack cane = new ItemStack(Material.SUGAR_CANE, NumberUtils.randomIntBetween(3, 6));
		
		ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
		meta.addEnchant(Enchantment.DEPTH_STRIDER, 3, true);
		book.setItemMeta(meta);
		
		for (Player online : game.getPlayers()) {
			online.addPotionEffect(vision);
			online.addPotionEffect(water);
			
			PlayerUtils.giveItem(online, chicken, cow, cane, book);
		}
		
		for (World world : game.getWorlds()) {
			if (world.getEnvironment() != Environment.NORMAL) {
				continue;
			}
			
			world.setThundering(false);
			world.setStorm(true);
		}
    }

	@EventHandler
    public void on(BlockFromToEvent event) {
		if (task == null) {
			return;
		}
		
        event.setCancelled(true);
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!isEnabled()) {
			sender.sendMessage(PREFIX + "Flooded is not enabled.");
			return true;
		}
		
		if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
			sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			return true;
		}
		
		if (args.length < 2) {
			sender.sendMessage(PREFIX + "Usage: /flood <world> <diameter>");
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

		PlayerUtils.broadcast(PREFIX + "Starting flooded generation in world '§a" + world.getName() + "§7'.");
		
		task = new BukkitRunnable() {
			public void run() {
				if (locs.size() == 0) {
					PlayerUtils.broadcast(PREFIX + "The flooded generation has finished.");
					
					for (Entity entity : world.getEntities()) {
						if (entity instanceof Item) {
							entity.remove();
						}
					}
					
					cancel();
					task = null;
					return;
				}

				Location loc = locs.remove(locs.size() - 1);
				Chunk chunk = world.getChunkAt(loc);
				
				for (int y = 30; y <= 70; y++) {
					for (int x = 0; x < 16; x++) {
						for (int z = 0; z < 17; z++) {
							Block block = chunk.getBlock(x, y, z);
							
							if (block.getType() == Material.GRASS && y != 70) {
								block.setType(Material.DIRT);
							}
							
							if (!block.getType().isSolid() && !block.isLiquid()) {
								block.setType(Material.WATER);
							}
						}
					}
				}

				double completed = ((totalChunks - ((double) locs.size())) * 100 / totalChunks);
				
				for (Player online : Bukkit.getOnlinePlayers()) {
					PacketUtils.sendAction(online, PREFIX + "Generating flooded §8(§a" + NumberUtils.formatPercentDouble(completed) + "%§8)");
				}
			}
		};
		
		task.runTaskTimer(plugin, 1, 1);
		return true;
	}
}