package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Genie scenario class
 * 
 * @author LeonTG77
 */
public class Genie extends Scenario implements Listener, CommandExecutor {
	private Map<String, Integer> wishes = new HashMap<String, Integer>();
	private Map<String, Integer> kills = new HashMap<String, Integer>();
	private Set<String> dead = new HashSet<String>();
	
	private static final String LINE = "§7------------------------------------------------";
	private static final String PREFIX = "§8[§9Genie§8] §f";

	public Genie() {
		super("Genie", "You have three wishes throughout the whole game, but what you can wish for depends on the amount of kills you have at the time. So basically, you can't wish for something from a lower kill list if you've gotten more kills than that. Ex: If you wanted a golden apple from the 0 kill wishlist, but since you have 1 kill to your name, you can't. You can only wish for things from the 1 kill wishlist.");
		
		Bukkit.getPluginCommand("genie").setExecutor(this);
	}

	@Override
	public void onDisable() {
		wishes.clear();
		kills.clear();
		dead.clear();
	}

	@Override
	public void onEnable() {
		for (OfflinePlayer whitelisted : Bukkit.getWhitelistedPlayers()) {
			wishes.put(whitelisted.getName(), 3);
			kills.put(whitelisted.getName(), 0);
		}
		
		dead.clear();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if (dead.contains(player.getName())) {
			return;
		}
		
		if (!wishes.containsKey(player.getName())) {
			wishes.put(player.getName(), 3);
		}
		
		if (!kills.containsKey(player.getName())) {
			kills.put(player.getName(), 0);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		Player player = event.getEntity();
		
		wishes.remove(player.getName());
		kills.remove(player.getName());
		dead.add(player.getName());
		
		Player killer = player.getKiller();
		
		if (killer == null) {
			return;
		}
		
		if (!kills.containsKey(killer.getName())) {
			kills.put(killer.getName(), 0);
		}
		
		if (kills.get(killer.getName()) == 5) {
			killer.sendMessage(LINE);
			killer.sendMessage(PREFIX + "Your killstreak §ecannot§r go any higher");
			killer.sendMessage(LINE);
			killer.playSound(killer.getLocation(), "random.break", 1, 1);
		}
		else {
			kills.put(killer.getName(), kills.get(killer.getName()) + 1);
			killer.sendMessage(LINE);
			killer.sendMessage(PREFIX + "Your killstreak is now §e" + kills.get(killer.getName()));
			killer.sendMessage(LINE);
			killer.playSound(killer.getLocation(), "note.harp", 1, 1);
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can wish for genie items.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (!isEnabled()) {
			player.sendMessage(PREFIX + "Genie is not enabled.");
			return true;
		}
		
		if (!State.isState(State.INGAME)) {
			player.sendMessage(PREFIX + "The game hasn't started yet.");
			return true;
		}
		
		if (args.length == 0) {
			player.sendMessage(LINE);
			player.sendMessage(PREFIX + "§eCommands:");
			player.sendMessage(PREFIX + "/genie wishes §7- How many wishes you have");
			player.sendMessage(PREFIX + "/genie wish §7- Wish for a item here");
			player.sendMessage(LINE);
			return true;
		}
		
		if (!wishes.containsKey(player.getName())) {
			wishes.put(player.getName(), 3);
		}
		
		if (!kills.containsKey(player.getName())) {
			kills.put(player.getName(), 0);
		}
		
		if (args[0].equalsIgnoreCase("wishes")) {
			if (args.length > 1 && args[1].equalsIgnoreCase("list")) {
				if (args.length < 3) {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You are at §e" + kills.get(player.getName()) + "§r kills");
					player.sendMessage(PREFIX + "/genie wishes list 0 §7- 0 Kills");
					player.sendMessage(PREFIX + "/genie wishes list 1 §7- 1 Kill");
					player.sendMessage(PREFIX + "/genie wishes list 2 §7- 2 Kills");
					player.sendMessage(PREFIX + "/genie wishes list 3 §7- 3 Kills");
					player.sendMessage(PREFIX + "/genie wishes list 4 §7- 4 Kills");
					player.sendMessage(PREFIX + "/genie wishes list 5 §7- 5+ Kills");
					player.sendMessage(LINE);
				} 
				else {
					if (args[2].equalsIgnoreCase("0")) {
						player.sendMessage(LINE);
						player.sendMessage(PREFIX + "§eTier 0:");
						player.sendMessage(PREFIX + "Golden Apple §7- /genie wish gapple");
						player.sendMessage(PREFIX + "Diamond Sword §7- /genie wish dsword");
						player.sendMessage(PREFIX + "Anvil §7- /genie wish anvil");
						player.sendMessage(LINE);
					} 
					else if (args[2].equalsIgnoreCase("1")) {
						player.sendMessage(LINE);
						player.sendMessage(PREFIX + "§eTier 1:");
						player.sendMessage(PREFIX + "Player Head §7- /genie wish head");
						player.sendMessage(PREFIX + "Tier I Speed Pot §7- /genie wish speed1");
						player.sendMessage(PREFIX + "Tier I Strength Pot §7- /genie wish strength");
						player.sendMessage(LINE);
					} 
					else if (args[2].equalsIgnoreCase("2")) {
						player.sendMessage(LINE);
						player.sendMessage(PREFIX + "§eTier 2:");
						player.sendMessage(PREFIX + "Enchanting Table §7- /genie wish etable");
						player.sendMessage(PREFIX + "Brewing Stand §7- /genie wish bstand");
						player.sendMessage(PREFIX + "Fortune III Book §7- /genie wish fortune");
						player.sendMessage(LINE);
					} 
					else if (args[2].equalsIgnoreCase("3")) {
						player.sendMessage(LINE);
						player.sendMessage(PREFIX + "§eTier 3:");
						player.sendMessage(PREFIX + "15 Bookshelves §7- /genie wish bookshelf");
						player.sendMessage(PREFIX + "5 Diamond Ore §7- /genie wish dore");
						player.sendMessage(PREFIX + "Tier II Speed Pot §7- /genie wish speed2");
						player.sendMessage(PREFIX + "8 Nether Warts §7- /genie wish netherwart");
						player.sendMessage(LINE);
					} 
					else if (args[2].equalsIgnoreCase("4")) {
						player.sendMessage(LINE);
						player.sendMessage(PREFIX + "§eTier 4:");
						player.sendMessage(PREFIX + "Tier II Health Pot §7- /genie wish health");
						player.sendMessage(PREFIX + "128 Bottles of Enchanting §7- /genie wish ebottle");
						player.sendMessage(PREFIX + "Glowstone Block §7- /genie wish glowstone");
						player.sendMessage(PREFIX + "Blaze Rod §7- /genie wish blazerod");
						player.sendMessage(LINE);
					} 
					else if (args[2].equalsIgnoreCase("5")) {
						player.sendMessage(LINE);
						player.sendMessage(PREFIX + "§eHighest Tier(5+):");
						player.sendMessage(PREFIX + "64 Obsidian §7- /genie wish obsidian");
						player.sendMessage(PREFIX + "3 Wither Skulls §7- /genie wish skull");
						player.sendMessage(PREFIX + "5 Soul Sand §7- /genie wish sand");
						player.sendMessage(PREFIX + "8 Gold Ingots §7- /genie wish gold");
						player.sendMessage(LINE);
					} 
					else {
						player.sendMessage(LINE);
						player.sendMessage(PREFIX + "§cWrong Syntax - Use /genie wishes list");
						player.sendMessage(LINE);
						player.playSound(player.getLocation(), "random.break", 1, 1);
					}
				}
			} 
			else {
				if (wishes.get(player.getName()) == 0) {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cYou have no more wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				} 
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You have §e" + wishes.get(player.getName()) + "§r wishes left");
					player.sendMessage(PREFIX + "You are at §e" + kills.get(player.getName()) + "§r kills");
					player.sendMessage(PREFIX + "Here is your list:");
					player.sendMessage(LINE);
					
					if (kills.get(player.getName()) == 0) {
						player.sendMessage(PREFIX + "§eTier 0:");
						player.sendMessage(PREFIX + "Golden Apple §7- /genie wish gapple");
						player.sendMessage(PREFIX + "Diamond Sword §7- /genie wish dsword");
						player.sendMessage(PREFIX + "Anvil §7- /genie wish anvil");
					} 
					else if (kills.get(player.getName()) == 1) {
						player.sendMessage(PREFIX + "§eTier 1:");
						player.sendMessage(PREFIX + "Player Head §7- /genie wish head");
						player.sendMessage(PREFIX + "Tier I Speed Pot §7- /genie wish speed1");
						player.sendMessage(PREFIX + "Tier I Strength Pot §7- /genie wish strength");
					} 
					else if (kills.get(player.getName()) == 2) {
						player.sendMessage(PREFIX + "§eTier 2:");
						player.sendMessage(PREFIX + "Enchanting Table §7- /genie wish etable");
						player.sendMessage(PREFIX + "Brewing Stand §7- /genie wish bstand");
						player.sendMessage(PREFIX + "Fortune III Book §7- /genie wish fortune");
					} 
					else if (kills.get(player.getName()) == 3) {
						player.sendMessage(PREFIX + "§eTier 3:");
						player.sendMessage(PREFIX + "15 Bookshelves §7- /genie wish bookshelf");
						player.sendMessage(PREFIX + "5 Diamond Ore §7- /genie wish dore");
						player.sendMessage(PREFIX + "Tier II Speed Pot §7- /genie wish speed2");
						player.sendMessage(PREFIX + "8 Nether Warts §7- /genie wish netherwart");
					} 
					else if (kills.get(player.getName()) == 4) {
						player.sendMessage(PREFIX + "§eTier 4:");
						player.sendMessage(PREFIX + "Tier II Health Pot §7- /genie wish health");
						player.sendMessage(PREFIX + "128 Bottles of Enchanting §7- /genie wish ebottle");
						player.sendMessage(PREFIX + "Glowstone Block §7- /genie wish glowstone");
						player.sendMessage(PREFIX + "Blaze Rod §7- /genie wish blazerod");
					} 
					else {
						player.sendMessage(PREFIX + "§eHighest Tier(5+):");
						player.sendMessage(PREFIX + "64 Obsidian §7- /genie wish obsidian");
						player.sendMessage(PREFIX + "3 Wither Skulls §7- /genie wish skull");
						player.sendMessage(PREFIX + "5 Soul Sand §7- /genie wish sand");
						player.sendMessage(PREFIX + "8 Gold Ingots §7- /genie wish gold");
					}
					
					player.sendMessage(PREFIX + "§e/genie wishes list §7- To learn all the other wishes");
					player.sendMessage(LINE);
				}
			}
		}
		else if (args[0].equalsIgnoreCase("wish")) {
			if (dead.contains(player.getName())) {
				player.sendMessage(LINE);
				player.sendMessage(PREFIX + "§cYou cannot wish when you are dead.");
				player.sendMessage(LINE);
				player.playSound(player.getLocation(), "random.break", 1, 1);
				return true;
			}
			
			if (wishes.get(player.getName()) == 0) {
				player.sendMessage(LINE);
				player.sendMessage(PREFIX + "§cError: You have §e0§c wishes left");
				player.sendMessage(LINE);
				player.playSound(player.getLocation(), "random.break", 1, 1);
				return true;
			}
			
			if (args.length < 2) {
				player.sendMessage(LINE);
				player.sendMessage(PREFIX + "§cError: Select a wish §7- /genie wishes");
				player.sendMessage(LINE);
				return true;
			}
		

			ItemStack item;
			
			// Killstreak 0
			
			if (args[1].equalsIgnoreCase("gapple")) {
				if (kills.get(player.getName()) == 0) {
					item = new ItemStack(Material.GOLDEN_APPLE);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			else if (args[1].equalsIgnoreCase("dsword")) {
				if (kills.get(player.getName()) == 0) {
					item = new ItemStack(Material.DIAMOND_SWORD);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			else if (args[1].equalsIgnoreCase("anvil")) {
				if (kills.get(player.getName()) == 0) {
					item = new ItemStack(Material.ANVIL);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			
			// Killstreak 1
			
			else if (args[1].equalsIgnoreCase("head")) {
				if (kills.get(player.getName()) == 1) {
					item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
					SkullMeta meta = (SkullMeta) item.getItemMeta();
					meta.setOwner(player.getName());
					item.setItemMeta(meta);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			else if (args[1].equalsIgnoreCase("speed1")) {
				if (kills.get(player.getName()) == 1) {
					item = new ItemStack(Material.POTION, 1, (short) 8194);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			else if (args[1].equalsIgnoreCase("strength")) {
				if (kills.get(player.getName()) == 1) {
					item = new ItemStack(Material.POTION, 1, (short) 8201);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			
			// Killstreak 2
			
			else if (args[1].equalsIgnoreCase("etable")) {
				if (kills.get(player.getName()) == 2) {
					item = new ItemStack(Material.ENCHANTMENT_TABLE);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			else if (args[1].equalsIgnoreCase("bstand")) {
				if (kills.get(player.getName()) == 2) {
					item = new ItemStack(Material.BREWING_STAND_ITEM);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			else if (args[1].equalsIgnoreCase("fortune")) {
				if (kills.get(player.getName()) == 2) {
					item = new ItemStack(Material.ENCHANTED_BOOK);
					EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
			        meta.addStoredEnchant(Enchantment.LOOT_BONUS_BLOCKS, 3, true);
			        item.setItemMeta(meta);
					
			        PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			
			// Killstreak 3

			else if (args[1].equalsIgnoreCase("bookshelf")) {
				if (kills.get(player.getName()) == 3) {
					item = new ItemStack(Material.BOOKSHELF, 15);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			else if (args[1].equalsIgnoreCase("dore")) {
				if (kills.get(player.getName()) == 3) {
					item = new ItemStack(Material.DIAMOND_ORE, 5);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			else if (args[1].equalsIgnoreCase("speed2")) {
				if (kills.get(player.getName()) == 3) {
					item = new ItemStack(Material.POTION, 1, (short) 8226);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			else if (args[1].equalsIgnoreCase("netherwart")) {
				if (kills.get(player.getName()) == 3) {
					item = new ItemStack(Material.NETHER_WARTS, 8);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			
			// Killstreak 4

			else if (args[1].equalsIgnoreCase("health")) {
				if (kills.get(player.getName()) == 4) {
					item = new ItemStack(Material.POTION, 1, (short) 8229);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			else if (args[1].equalsIgnoreCase("ebottle")) {
				if (kills.get(player.getName()) == 4) {
					item = new ItemStack(Material.EXP_BOTTLE, 128);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			else if (args[1].equalsIgnoreCase("glowstone")) {
				if (kills.get(player.getName()) == 4) {
					item = new ItemStack(Material.GLOWSTONE);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			else if (args[1].equalsIgnoreCase("blazerod")) {
				if (kills.get(player.getName()) == 4) {
					item = new ItemStack(Material.BLAZE_ROD);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			
			// Killstreak 5+

			else if (args[1].equalsIgnoreCase("obsidian")) {
				if (kills.get(player.getName()) == 5) {
					item = new ItemStack(Material.OBSIDIAN, 64);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			else if (args[1].equalsIgnoreCase("skull")) {
				if (kills.get(player.getName()) == 5) {
					item = new ItemStack(Material.SKULL_ITEM, 3, (short) 1);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			else if (args[1].equalsIgnoreCase("sand")) {
				if (kills.get(player.getName()) == 5) {
					item = new ItemStack(Material.SOUL_SAND, 5);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			else if (args[1].equalsIgnoreCase("gold")) {
				if (kills.get(player.getName()) == 5) {
					item = new ItemStack(Material.GOLD_INGOT, 8);
					
					PlayerUtils.giveItem(player, item);
					wishes.put(player.getName(), wishes.get(player.getName()) - 1);
					
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "note.harp", 1, 1);
				}
				else {
					player.sendMessage(LINE);
					player.sendMessage(PREFIX + "§cError: Incorrect Killstreak §7- /genie wishes");
					player.sendMessage(LINE);
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			}
			else {
				player.sendMessage(LINE);
				player.sendMessage(PREFIX + "§cError: Cannot find wish §7- /genie wishes");
				player.sendMessage(LINE);
				player.playSound(player.getLocation(), "random.break", 1, 1);
			}
			return true;
		}
		
		player.sendMessage(LINE);
		player.sendMessage(PREFIX + "§eCommands:");
		player.sendMessage(PREFIX + "/genie wishes §7- How many wishes you have");
		player.sendMessage(PREFIX + "/genie wish §7- Wish for a item here");
		player.sendMessage(LINE);
		return true;
	}
}