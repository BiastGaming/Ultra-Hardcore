package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * GlassWorld scenario class.
 * 
 * @author LeonTG77
 */
public class GlassWorld extends Scenario implements Listener, CommandExecutor {
	public static final String PREFIX = "§6Glass World §8» §7";
	
	private final Main plugin;
	private final Game game;
	
	/**
	 * GlassWorld class constructor.
	 * 
	 * @param plugin The main class.
	 * @param game The game class.
	 */
	public GlassWorld(Main plugin, Game game) {
		super("GlassWorld", "All grass blocks are replaced with green glass, all dirt blocks are replaced with brown glass blocks, all stone is replaced with gray glass, sand is replaced with yellow glass, nether rack is replaced with red glass and all bedrock is replaced by black glass, mining the glass still gives the dirt/cobblestone it should however the bedrock glass is not breakable. Leaves will also drop sugar canes.");
		
		this.plugin = plugin;
		this.game = game;
		
		plugin.getCommand("glassify").setExecutor(this);
	}
	
	private final List<Location> locs = new ArrayList<Location>();
	
	private BukkitRunnable task = null;
	private double totalChunks = 0;
	
	@Override
	public void onDisable() {
		if (task != null) {
			PlayerUtils.broadcast(PREFIX + "Cancelling glass world generation.");
			task.cancel();
		}
		
		totalChunks = 0;
		task = null;
	}

	@EventHandler
	public void LeavesDecayEvent(LeavesDecayEvent event) {
		Random r = new Random();
		
		if (r.nextInt(100) < 2) {
			Item item = event.getBlock().getWorld().dropItem(event.getBlock().getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.SUGAR_CANE, 1 + r.nextInt(1)));
			item.setVelocity(new Vector(0, 0.2, 0));
		}
	}
	
	@EventHandler
    public void on(BlockBreakEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        if (!game.getPlayers().contains(player) || player.getGameMode() != GameMode.SURVIVAL) {
        	return;
        }
        
        if (block.getType() != Material.STAINED_GLASS) {
        	return;
        }
        
        Location toDrop = block.getLocation().clone().add(0.5, 0.7, 0.5);
        ItemStack item = player.getItemInHand();
        
        switch (BlockUtils.getDurability(block)) {
        case 15:
        	player.sendMessage(PREFIX + "Hi I am bedrock, You can't mine me even if you wanted to!");
        	event.setCancelled(true);
        	break;
        case 8:
        	if (item != null && item.containsEnchantment(Enchantment.SILK_TOUCH)) {
            	BlockUtils.dropItem(toDrop, new ItemStack(Material.STONE));
        	} else {
            	BlockUtils.dropItem(toDrop, new ItemStack(Material.COBBLESTONE));
        	}
        	break;
        case 12:
        	BlockUtils.dropItem(toDrop, new ItemStack(Material.DIRT));
        	break;
        case 14:
        	BlockUtils.dropItem(toDrop, new ItemStack(Material.NETHERRACK));
        	break;
        case 4:
        	BlockUtils.dropItem(toDrop, new ItemStack(Material.SAND));
        	break;
        case 13:
        	if (item != null && item.containsEnchantment(Enchantment.SILK_TOUCH)) {
            	BlockUtils.dropItem(toDrop, new ItemStack(Material.GRASS));
        	} else {
            	BlockUtils.dropItem(toDrop, new ItemStack(Material.DIRT));
        	}
        	break;
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
			sender.sendMessage(PREFIX + "Glass World is not enabled.");
			return true;
		}
		
		if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
			sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			return true;
		}
		
		if (args.length < 2) {
			sender.sendMessage(PREFIX + "Usage: /glassify <world> <diameter>");
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

		PlayerUtils.broadcast(PREFIX + "Starting glass world generation in world '§a" + world.getName() + "§7'.");
		
		task = new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				if (locs.size() == 0) {
					PlayerUtils.broadcast(PREFIX + "The glass world generation has finished.");
					
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
				
				for (int y = 0; y <= 128; y++) {
					for (int x = 0; x < 16; x++) {
						for (int z = 0; z < 17; z++) {
							Block block = chunk.getBlock(x, y, z);
							
							switch (block.getType()) {
							case STONE:
								block.setType(Material.STAINED_GLASS);
								block.setData((byte) 8);
								break;
							case BEDROCK:
								block.setType(Material.STAINED_GLASS);
								block.setData((byte) 15);
								break;
							case DIRT:
								block.setType(Material.STAINED_GLASS);
								block.setData((byte) 12);
								break;
							case NETHERRACK:
								block.setType(Material.STAINED_GLASS);
								block.setData((byte) 14);
								break;
							case SAND:
								block.setType(Material.STAINED_GLASS);
								block.setData((byte) 4);
								break;
							case GRASS:
								block.setType(Material.STAINED_GLASS);
								block.setData((byte) 13);
								break;
							default:
								break;
							}
						}
					}
				}

				double completed = ((totalChunks - ((double) locs.size())) * 100 / totalChunks);
				
				for (Player online : Bukkit.getOnlinePlayers()) {
					PacketUtils.sendAction(online, PREFIX + "Generating glass world §8(§a" + NumberUtils.formatPercentDouble(completed) + "%§8)");
				}
			}
		};
		
		task.runTaskTimer(plugin, 1, 1);
		return true;
	}
}