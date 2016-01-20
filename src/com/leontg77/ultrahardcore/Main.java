package com.leontg77.ultrahardcore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import net.minecraft.server.v1_8_R3.MinecraftServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.leontg77.ultrahardcore.Spectator.SpecInfo;
import com.leontg77.ultrahardcore.commands.CommandHandler;
import com.leontg77.ultrahardcore.inventory.InvGUI;
import com.leontg77.ultrahardcore.inventory.listener.ConfigListener;
import com.leontg77.ultrahardcore.inventory.listener.InvseeListener;
import com.leontg77.ultrahardcore.inventory.listener.SelectorListener;
import com.leontg77.ultrahardcore.listeners.BlockListener;
import com.leontg77.ultrahardcore.listeners.BuildProtectListener;
import com.leontg77.ultrahardcore.listeners.ChatListener;
import com.leontg77.ultrahardcore.listeners.DeathListener;
import com.leontg77.ultrahardcore.listeners.EntityListener;
import com.leontg77.ultrahardcore.listeners.InventoryListener;
import com.leontg77.ultrahardcore.listeners.LoginListener;
import com.leontg77.ultrahardcore.listeners.LogoutListener;
import com.leontg77.ultrahardcore.listeners.PlayerListener;
import com.leontg77.ultrahardcore.listeners.PortalListener;
import com.leontg77.ultrahardcore.listeners.SpectatorListener;
import com.leontg77.ultrahardcore.listeners.StatsListener;
import com.leontg77.ultrahardcore.listeners.WorldListener;
import com.leontg77.ultrahardcore.listeners.iPvPListener;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.PermissionsManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.protocol.HardcoreHearts;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.ubl.UBL;
import com.leontg77.ultrahardcore.ubl.UBLListener;
import com.leontg77.ultrahardcore.utils.FileUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;
import com.leontg77.ultrahardcore.world.AntiStripmine;
import com.leontg77.ultrahardcore.world.BiomeSwap;
import com.leontg77.ultrahardcore.world.WorldManager;

/**
 * Main class of the UHC plugin.
 * <p>
 * This class contains methods for prefixes, adding recipes, enabling and disabling.
 * 
 * @author LeonTG77
 */
public class Main extends JavaPlugin {
	public static Main plugin;

	public static final String NO_PERM_MSG = "�cYou don't have permission.";
	public static final String PREFIX = "�4�lUHC �8� �7";
	
	public static Map<String, int[]> rainbow = new HashMap<String, int[]>();
	
	public static Map<String, Integer> teamKills = new HashMap<String, Integer>();
	public static Map<String, Integer> kills = new HashMap<String, Integer>();

	public static Recipe melonRecipe;
	public static Recipe headRecipe;
	
	
	@Override
	public void onDisable() {
		PluginDescriptionFile file = getDescription();
		getLogger().info(file.getName() + " is now disabled.");
		
		BiomeSwap.getInstance().resetBiomes();
		saveData();
		
		plugin = null;
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile file = getDescription();
		getLogger().info(file.getName() + " v" + file.getVersion() + " is now enabled.");
		getLogger().info("The plugin was created by LeonTG77.");
		
		plugin = this;
		Settings.getInstance().setup();
	    
		WorldManager.getInstance().loadWorlds();
		
		AntiStripmine.getInstance().setup();
		BiomeSwap.getInstance().setup();

		Announcer.getInstance().setup();
		Arena.getInstance().setup();
		
		Parkour.getInstance().setup();

		ScenarioManager.getInstance().setup();
		UBL.getInstance().reload();
		
		BoardManager.getInstance().setup();
		TeamManager.getInstance().setup();
		
		FileUtils.updateUserFiles();
		
		InvGUI.getInstance().setup();
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Game game = Game.getInstance();
		
		recoverData();
		addRecipes();
		
		if (game.hardcoreHearts()) {
			HardcoreHearts.enable();
		}

		PluginManager manager = Bukkit.getPluginManager();
		
		// register all listeners.
		manager.registerEvents(new BlockListener(), this);
		manager.registerEvents(new BuildProtectListener(), this);
		manager.registerEvents(new ChatListener(), this);
		manager.registerEvents(new DeathListener(), this);
		manager.registerEvents(new EntityListener(), this);
		manager.registerEvents(new InventoryListener(), this);
		manager.registerEvents(new iPvPListener(), this);
		manager.registerEvents(new LoginListener(), this);
		manager.registerEvents(new LogoutListener(), this);
		manager.registerEvents(new PlayerListener(), this);
		manager.registerEvents(new PortalListener(), this);
		manager.registerEvents(new StatsListener(), this);
		manager.registerEvents(new WorldListener(), this);
		manager.registerEvents(new UBLListener(), this);

		// register all inventory listeners.
		manager.registerEvents(new ConfigListener(), this);
		manager.registerEvents(new InvseeListener(), this);
		manager.registerEvents(new SelectorListener(), this);
		manager.registerEvents(new SpectatorListener(), this);

		// register all commands.
		new CommandHandler().registerCommands();
		
		switch (State.getState()) {
		case NOT_RUNNING:
			FileUtils.deletePlayerDataAndStats();
			Bukkit.setIdleTimeout(60);
			break;
		case INGAME:
			manager.registerEvents(new SpecInfo(), this);
			Bukkit.setIdleTimeout(10);
			break;
		default:
			break;
		}
		
		for (Player online : PlayerUtils.getPlayers()) {	
			PermissionsManager.addPermissions(online);
		}
		
		new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PlayerInventory inv = online.getInventory();
					
					ItemStack hemlet = inv.getHelmet();
					ItemStack chestplate = inv.getChestplate();
					ItemStack leggings = inv.getLeggings();
					ItemStack boots = inv.getBoots();
					
					if (hemlet != null && hemlet.getType() == Material.LEATHER_HELMET) {
						inv.setHelmet(rainbowArmor(online, hemlet));
					}
					
					if (chestplate != null && chestplate.getType() == Material.LEATHER_CHESTPLATE) {
						inv.setChestplate(rainbowArmor(online, chestplate));
					}
					
					if (leggings != null && leggings.getType() == Material.LEATHER_LEGGINGS) {
						inv.setLeggings(rainbowArmor(online, leggings));
					}
					
					if (boots != null && boots.getType() == Material.LEATHER_BOOTS) {
						inv.setBoots(rainbowArmor(online, boots));
					}
					
					String percentString = NumberUtils.makePercent(online.getHealth());
					Game game = Game.getInstance();
					
					if (game.tabShowsHealthColor() && !Spectator.getInstance().isSpectating(online)) {
						String percentColor = percentString.substring(0, 2);
					    
					    online.setPlayerListName(percentColor + online.getName());
					}

					int percent = Integer.parseInt(percentString.substring(2));
					Scoreboard sb = BoardManager.getInstance().board;
					
					Objective bellowName = sb.getObjective("nameHealth");
					Objective tabList = sb.getObjective("tabHealth");
					
					if (tabList != null) {
						Score score = tabList.getScore(online.getName());
						score.setScore(percent);
					}
					
					if (bellowName != null) {
						Score score = bellowName.getScore(online.getName());
						score.setScore(percent);
					}
				}
				
				for (World world : Bukkit.getWorlds()) {
					if (world.getName().equals("lobby")) {
						if (world.getDifficulty() != Difficulty.PEACEFUL) {
							world.setDifficulty(Difficulty.PEACEFUL);
						}
						
						if (world.getTime() != 18000) {
							world.setTime(18000);
						}
						continue;
					}
					
					if (world.getName().equals("arena")) {
						if (world.getDifficulty() != Difficulty.HARD) {
							world.setDifficulty(Difficulty.HARD);
						}
						
						if (world.getTime() != 6000) {
							world.setTime(6000);
						}
						continue;
					}
					
					if (world.getDifficulty() != Difficulty.HARD) {
						world.setDifficulty(Difficulty.HARD);
					}
				}
			}
		}.runTaskTimer(this, 1, 1);
	}
	
	/**
	 * Gets the servers tps.
	 * 
	 * @return The servers tps.
	 */
	public static double getTps() {
		double tps = MinecraftServer.getServer().recentTps[0];
		String converted = NumberUtils.formatDouble(tps);
		
		return Double.parseDouble(converted);
	}
	
	/**
	 * Get the spawnpoint of the lobby.
	 * 
	 * @return The lobby spawnpoint.
	 */
	public static Location getSpawn() {
		Settings settings = Settings.getInstance();
		
		World world = Bukkit.getWorld(settings.getData().getString("spawn.world", "lobby"));
		
		if (world == null) {
			world = Bukkit.getWorlds().get(0);
		}
		
		double x = settings.getData().getDouble("spawn.x", 0.5);
		double y = settings.getData().getDouble("spawn.y", 33.0);
		double z = settings.getData().getDouble("spawn.z", 0.5);
		float yaw = (float) settings.getData().getDouble("spawn.yaw", 0);
		float pitch = (float) settings.getData().getDouble("spawn.pitch", 0);
		
		Location loc = new Location(world, x, y, z, yaw, pitch);
		return loc;
	}
	
	/**
	 * Adds the golden head recipe.
	 */
	public void addRecipes() {
        ItemStack head = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD  + "Golden Head");
        meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Some say consuming the head of a", ChatColor.DARK_PURPLE + "fallen foe strengthens the blood."));
        head.setItemMeta(meta); 

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        
        ShapedRecipe goldenmelon = new ShapedRecipe(new ItemStack(Material.SPECKLED_MELON)).shape("@@@", "@*@", "@@@").setIngredient('@', Material.GOLD_INGOT).setIngredient('*', Material.MELON);
        ShapedRecipe goldenhead = new ShapedRecipe(head).shape("@@@", "@*@", "@@@").setIngredient('@', Material.GOLD_INGOT).setIngredient('*', skull.getData());
        
        ShapelessRecipe ironplate = new ShapelessRecipe(new ItemStack(Material.IRON_INGOT, 2)).addIngredient(Material.IRON_PLATE);
        ShapelessRecipe goldplate = new ShapelessRecipe(new ItemStack(Material.GOLD_INGOT, 2)).addIngredient(Material.GOLD_PLATE);

        getServer().addRecipe(goldenmelon);
        getServer().addRecipe(goldenhead);
        getServer().addRecipe(ironplate);
        getServer().addRecipe(goldplate);

        melonRecipe = goldenmelon;
        headRecipe = goldenhead;
	}
	
	/**
	 * Save all the data to the data file.
	 */
	public void saveData() {
		List<String> scenarios = new ArrayList<String>();
		Settings settings = Settings.getInstance();
		
		for (Scenario scen : ScenarioManager.getInstance().getEnabledScenarios()) {
			scenarios.add(scen.getName());
		}
		
		settings.getData().set("scenarios", scenarios);
		
		for (Entry<String, Integer> tkEntry : teamKills.entrySet()) {
			settings.getData().set("teams.kills." + tkEntry.getKey(), tkEntry.getValue());
		}
		
		for (Entry<String, Integer> kEntry : kills.entrySet()) {
			settings.getData().set("kills." + kEntry.getKey(), kEntry.getValue());
		}
		
		for (Entry<String, Set<String>> entry : TeamManager.getInstance().getSavedTeams().entrySet()) {
			settings.getData().set("teams.data." + entry.getKey(), new ArrayList<String>(entry.getValue()));
		}
		
		settings.saveData();
	}
	
	/**
	 * Recover all the data from the data files.
	 */
	public void recoverData() {
		Settings settings = Settings.getInstance();
		State state;
		
		try {
			state = State.valueOf(settings.getData().getString("state"));
		} catch (Exception e) {
			state = State.NOT_RUNNING;
		}
		
		State.setState(state);
		
		try {
			for (String name : settings.getData().getConfigurationSection("kills").getKeys(false)) {
				kills.put(name, settings.getData().getInt("kills." + name));
			}
		} catch (Exception e) {
			getLogger().warning("Could not recover kills data.");
		}
		
		try {
			for (String name : settings.getData().getConfigurationSection("teams.kills").getKeys(false)) {
				teamKills.put(name, settings.getData().getInt("teams.kills" + name));
			}
		} catch (Exception e) {
			getLogger().warning("Could not recover team kills data.");
		}
		
		try {
			if (settings.getData().getConfigurationSection("team") != null) {
				for (String name : settings.getData().getConfigurationSection("teams.data").getKeys(false)) {
					TeamManager.getInstance().getSavedTeams().put("teams.data." + name, new HashSet<String>(settings.getData().getStringList("teams.data." + name)));
				}
			}
		} catch (Exception e) {
			getLogger().warning("Could not recover team data.");
		}
		
		try {
			for (String scen : settings.getData().getStringList("scenarios")) {
				ScenarioManager.getInstance().getScenario(scen).setEnabled(true);
			}
		} catch (Exception e) {
			getLogger().warning("Could not recover scenario data.");
		}
	}
	
	/**
	 * Change the color of the given type to a rainbow color.
	 *  
	 * @param player the players armor.
	 * @param type the type.
	 * @return The new colored leather armor.
	 */
	public ItemStack rainbowArmor(Player player, ItemStack item) {
		if (!rainbow.containsKey(player.getName())) {
			rainbow.put(player.getName(), new int[] { 0, 0, 255 });
		}
		
		int[] rain = rainbow.get(player.getName());
			
		int blue = rain[0];
		int green = rain[1];
		int red = rain[2];		

		if (red == 255 && blue == 0) {
			green++;
		}
			
		if (green == 255 && blue == 0) {
			red--;
		}
		
		if (green == 255 && red == 0) {
			blue++;
		}
			
		if (blue == 255 && red == 0) {
			green--;
		}
			
		if (green == 0 && blue == 255) {
			red++;
		}
			
		if (green == 0 && red == 255) {
			blue--;
		}
			
		rainbow.put(player.getName(), new int[] { blue, green, red });

    	ItemStack armor = new ItemStack (item.getType(), item.getAmount(), item.getDurability());
		LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
		meta.setColor(Color.fromBGR(blue, green, red));
		meta.setDisplayName(item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : null);
		meta.setLore(item.hasItemMeta() && item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<String>());
		
		for (Entry<Enchantment, Integer> ench : item.getEnchantments().entrySet()) {
			meta.addEnchant(ench.getKey(), ench.getValue(), true);
		}
		
		armor.setItemMeta(meta);
		return armor;
	}
	
	/**
	 * Border Shrink enum module.
	 * <p>
	 * Contains all the possible events when the border should shrink.
	 * 
	 * @author LeonTG77
	 */
	public enum BorderShrink {
		NEVER(""), START("from "), PVP("at "), MEETUP("at ");
		
		private String preText;
		
		/**
		 * Constructor for BorderShrink.
		 * 
		 * @param preText The text that fits before the shink name.
		 */
		private BorderShrink(String preText) {
			this.preText = preText;
		}
		
		/**
		 * Get the border pre text.
		 * 
		 * @return Pre text.
		 */
		public String getPreText() {
			return preText;
		}
	}
}