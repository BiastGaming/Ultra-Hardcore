package com.leontg77.ultrahardcore;
 
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.gui.guis.GameInfoGUI;
import com.leontg77.ultrahardcore.managers.PermissionsManager;
import com.leontg77.ultrahardcore.utils.FileUtils;

/**
 * User class.
 * <p>
 * This class contains methods for setting and getting stats, ranks, mute status and getting/saving/reloading the data file etc.
 * 
 * @author LeonTG77
 */
public class User {
	public static File folder;
	
	private final Player player;
	private final String uuid;
	
	private final Main plugin;
	private final Game game;
	
	private final PermissionsManager perm;
	private final GUIManager gui;
	
	private FileConfiguration config;
	private File file;
	
	/**
	 * Constuctor for player data.
	 * <p>
	 * This will set up the data for the player and create missing data.
	 */
	protected User(Main plugin, Game game, GUIManager gui, PermissionsManager perm, Player player, String uuid) {
		folder = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);
		
        this.player = player;
        this.uuid = uuid;
        
		this.plugin = plugin;
		this.game = game;
        
		this.gui = gui;
		this.perm = perm;
		
        if (!plugin.getDataFolder().exists()) {
        	plugin.getDataFolder().mkdir();
        }
        
        if (!folder.exists()) {
        	folder.mkdir(); 
        }
        
        file = new File(folder, uuid + ".yml");
        
        if (!file.exists()) {
        	try {
        		file.createNewFile();
        		creating = true;
        	} catch (Exception e) {
        		plugin.getLogger().severe(ChatColor.RED + "Could not create " + uuid + ".yml!");
        	}
        }
               
        config = YamlConfiguration.loadConfiguration(file);
        
        if (creating) {
        	if (player != null) {
            	config.set("username", player.getName());
            	config.set("uuid", player.getUniqueId().toString());
            	config.set("ip", player.getAddress().getAddress().getHostAddress());
        	}
        	
        	config.set("firstjoined", new Date().getTime());
        	config.set("lastlogin", new Date().getTime());
        	config.set("lastlogout", -1l);
        	config.set("rank", Rank.DEFAULT.name());
        	
			config.set("muted.status", false);
			config.set("muted.reason", "NOT_MUTED");
			config.set("muted.time", -1);
			
			for (Stat stats : Stat.values()) {
	        	config.set("stats." + stats.name().toLowerCase(), 0);
			}
			
        	saveFile();
        }
	}
    
    private boolean creating = false;
	
	/**
	 * Get the given player's ping.
	 * 
	 * @return the players ping
	 */
	public int getPing() {
		final CraftPlayer craft = (CraftPlayer) player;
		
		return craft.getHandle().ping;
	} 
	
	/**
	 * Check if the user hasn't been welcomed to the server.
	 * 
	 * @return True if he hasn't, false otherwise.
	 */
	public boolean isNew() {
		return creating;
	}
	
	/**
	 * Get the player class for the user.
	 * 
	 * @return The player class.
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Get the uuid for the user.
	 * 
	 * @return The uuid.
	 */
	public String getUUID() {
		return uuid;
	}
	
	/**
	 * Get the configuration file for the player.
	 * 
	 * @return The configuration file.
	 */
	public FileConfiguration getFile() {
		return config;
	}
	
	/**
	 * Save the config file.
	 */
	public void saveFile() {
		try {
			for (FileConfiguration file : FileUtils.getUserFiles()) {
				if (file.getString("uuid", "none").equals(config.getString("uuid", "none"))) {
					FileUtils.getUserFiles().remove(file);
					break;
				}
			}
			
			config.save(file);
			
    		FileUtils.getUserFiles().add(config);
		} catch (Exception e) {
    		plugin.getLogger().severe(ChatColor.RED + "Could not save " + file.getName() + "!");
		}
	}
	
	/**
	 * Reload the config file.
	 */
	public void reloadFile() {
        config = YamlConfiguration.loadConfiguration(file);
	}
	
	/**
	 * Set the rank for the player.
	 * 
	 * @param rank The new rank.
	 */
	public void setRank(Rank rank) {
		config.set("rank", rank.name());
		saveFile();
		
		GameInfoGUI info = gui.getGUI(GameInfoGUI.class);
		
		info.updateStaff();
		
		if (player != null) {
			perm.removePermissions(player);
			perm.addPermissions(player);
		}
	}

	/**
	 * Get the rank of the player.
	 * 
	 * @return the rank.
	 */
	public Rank getRank() {
		Rank rank;
		
		try {
			rank = Rank.valueOf(config.getString("rank"));
		} catch (Exception e) {
			rank = Rank.DEFAULT;
		}
		
		return rank;
	}

	/**
	 * Get the color of the rank the player has.
	 * 
	 * @return The rank color.
	 */
	public String getRankColor() {
		if (game.isRecordedRound()) {
			return "�7";
		}
		
		switch (getRank()) {
		case DONATOR:
			return "�a";
		case HOST:
		case TRIAL:
			return "�4";
		case OWNER:
			if (uuid.equals("02dc5178-f7ec-4254-8401-1a57a7442a2f")) {
				return "�3�o";
			} else {
				return "�4�o";
			}
		case STAFF:
			return "�c";
		default:
			return "�7";
		}
	}
	
	/**
	 * Get all possible alt accounts of the user.
	 * <p>
	 * These are colored: Red = Banned, Green = Online and Gray = Offline.
	 * 
	 * @return A list of alt accounts.
	 */
	public Set<String> getAlts() {
		Set<String> altList = new HashSet<String>();
		
		String thisName = config.getString("username", "none1");
		String thisIP = config.getString("ip", "none1");
		
		BanList banlist = Bukkit.getBanList(Type.NAME);
		
		for (FileConfiguration file : FileUtils.getUserFiles()) {
			String name = file.getString("username", "none2");
			String IP = file.getString("ip", "none2");
			
			if (!thisIP.equals(IP)) {
				continue;
			}
			
			if (thisName.equals(name)) {
				continue;
			}
			
			Player check = Bukkit.getPlayerExact(name);
			
			if (banlist.getBanEntry(player.getName()) != null) {
				altList.add("�4" + name + "�8");
			}
			else if (check != null) {
				altList.add("�a" + name + "�8");
			} 
			else {
				altList.add("�c" + name + "�8");
			}
		}
		
		return altList;
	}
	
	/**
	 * Set the death location of the player.
	 * 
	 * @param location The death loc.
	 */
	public void setDeathLoc(Location loc) {
		if (loc == null) {
			config.set("locs.death", null);
	        saveFile();
	        return;
		}
		
		config.set("locs.death.world", loc.getWorld().getName());
		config.set("locs.death.x", loc.getX());
		config.set("locs.death.y", loc.getY());
		config.set("locs.death.z", loc.getZ());
        saveFile();
	}
	
	/**
	 * Get the death location of the player.
	 * 
	 * @return The death location.
	 */
	public Location getDeathLoc() {
		if (!config.contains("locs.death")) {
			return null;
		}
		
		World world = Bukkit.getWorld(config.getString("locs.death.world"));
		
		if (world == null) {
			return null;
		}
		
		double x = config.getDouble("locs.death.x");
		double y = config.getDouble("locs.death.y");
		double z = config.getDouble("locs.death.z");
		
		Location loc = new Location(world, x, y, z);
		
		return loc;
	}
	
	/**
	 * Set the last location of the player.
	 * 
	 * @param location The last loc.
	 */
	public void setLastLoc(Location loc) {
		if (loc == null) {
			config.set("locs.last", null);
	        saveFile();
	        return;
		}
		
		config.set("locs.last.world", loc.getWorld().getName());
		config.set("locs.last.x", loc.getX());
		config.set("locs.last.y", loc.getY());
		config.set("locs.last.z", loc.getZ());
		config.set("locs.last.yaw", loc.getYaw());
		config.set("locs.last.pitch", loc.getPitch());
        saveFile();
	}
	
	/**
	 * Get the last location of the player.
	 * 
	 * @return The last location.
	 */
	public Location getLastLoc() {
		if (!config.contains("locs.last")) {
			return null;
		}
		
		World world = Bukkit.getWorld(config.getString("locs.last.world"));
		
		if (world == null) {
			return null;
		}
		
		double x = config.getDouble("locs.last.x");
		double y = config.getDouble("locs.last.y");
		double z = config.getDouble("locs.last.z");
		float yaw = (float) config.getDouble("locs.last.yaw", 0);
		float pitch = (float) config.getDouble("locs.last.pitch", 0);
		
		Location loc = new Location(world, x, y, z, yaw, pitch);
		
		return loc;
	}
	
	private static final String IGNORE_PATH = "ignoreList";
	
	/**
	 * Start ignoring the given player.
	 * 
	 * @param player The player to ignore
	 */
	public void ignore(Player player) {
		final List<String> ignoreList = config.getStringList(IGNORE_PATH);
		ignoreList.add(player.getUniqueId().toString());
		
		config.set(IGNORE_PATH, ignoreList);
		saveFile();
	}

	/**
	 * Stop ignoring the given player.
	 * 
	 * @param player The player to stop ignoring
	 */
	public void unIgnore(Player player) {
		final List<String> ignoreList = config.getStringList(IGNORE_PATH);
		ignoreList.remove(player.getUniqueId().toString());
		
		config.set(IGNORE_PATH, ignoreList);
		saveFile();
	}
	
	/**
	 * Check if the this User is ignoring the given player.
	 * 
	 * @param player The player checking.
	 * @return True if he is, false otherwise.
	 */
	public boolean isIgnoring(Player player) {
		if (getRank().getLevel() >= Rank.STAFF.getLevel()) {
			return false;
		}
		
		return config.getStringList(IGNORE_PATH).contains(player.getUniqueId().toString());
	}
	
	/**
	 * Mute the user.
	 * 
	 * @param reason The reason of the mute.
	 * @param unmute The date of unmute, null if permanent.
	 */
	public void mute(String reason, Date unmute) {
		config.set("muted.status", true);
		config.set("muted.reason", reason);
		
		if (unmute == null) {
			config.set("muted.time", -1);
		} else {
			config.set("muted.time", unmute.getTime());
		}
		
		saveFile();
	}
	
	/**
	 * Unmute the user.
	 */
	public void unmute() {
		config.set("muted.status", false);
		config.set("muted.reason", "NOT_MUTED");
		config.set("muted.time", -1);
		saveFile();
	}
	
	/**
	 * Check if the player is muted.
	 * 
	 * @return <code>true</code> if the player is muted, <code>false</code> otherwise.
	 */
	public boolean isMuted() {
		Date date = new Date();
		
		// if the mute isnt permanent (perm == -1) and their mute time experied, return false and unmute.
		if (getMuteExpiration() != null && getMuteExpiration().getTime() < date.getTime()) {
			unmute();
		} 
		
		return config.getBoolean("muted.status", false);
	}
	
	/**
	 * Get the reason the player is muted.
	 * 
	 * @return The reason of the mute, null if not muted.
	 */
	public String getMutedReason() {
		return config.getString("muted.reason", "NOT_MUTED");
	}
	
	/**
	 * Get the time in milliseconds for the unmute.
	 * 
	 * @return The unmute time.
	 */
	public Date getMuteExpiration() {
		final long unmute = config.getLong("muted.time", -1);
		
		if (unmute == -1) {
			return null;
		}
		
		return new Date(unmute);
	}
	
	/**
	 * Set the given stat to a new value
	 * 
	 * @param stat The stat setting.
	 * @param value The new value.
	 */
	public void setStat(Stat stat, double value) {
		if (game.isRecordedRound() || game.isPrivateGame()) {
			return;
		}
		
		final String statName = stat.name().toLowerCase();
		
		if (stat == Stat.ARENADEATHS || stat == Stat.ARENAKILLSTREAK || stat == Stat.ARENAKILLS) {
			if (!Bukkit.hasWhitelist()) {
				config.set("stats." + statName, value);
				saveFile();
			}
		} else {
			if (State.isState(State.INGAME)) {
				config.set("stats." + statName, value);
				saveFile();
			}
		}
	}
	
	/**
	 * Increase the given stat by 1.
	 * 
	 * @param stat the stat increasing.
	 */
	public void increaseStat(Stat stat) {
		if (game.isRecordedRound() || game.isPrivateGame()) {
			return;
		}
		
		final String statName = stat.name().toLowerCase();
		final double current = config.getDouble("stats." + statName, 0);
		
		if (stat == Stat.ARENADEATHS || stat == Stat.ARENAKILLSTREAK || stat == Stat.ARENAKILLS) {
			if (!Bukkit.hasWhitelist()) {
				config.set("stats." + statName, current + 1);
				saveFile();
			}
		} else {
			if (State.isState(State.INGAME) || stat == Stat.WINS) {
				config.set("stats." + statName, current + 1);
				saveFile();
			}
		}
	}
	
	/**
	 * Get the amount from the given stat as a int.
	 * 
	 * @param stat the stat getting.
	 * @return The amount in a int form.
	 */
	public int getStat(Stat stat) {
		return config.getInt("stats." + stat.name().toLowerCase(), 0);
	}
	
	/**
	 * Get the amount from the given stat as a double.
	 * 
	 * @param stat the stat getting.
	 * @return The amount in a double form.
	 */
	public double getStatDouble(Stat stat) {
		return config.getDouble("stats." + stat.name().toLowerCase(), 0);
	}
	
	/**
	 * Reset the players health, food, xp, inventory and effects.
	 */
	public void reset() {
        resetHealth();
        resetFood();
        resetExp();
        resetInventory();
        resetEffects();
    }

	/**
	 * Reset the players effects.
	 */
    public void resetEffects() {
        Collection<PotionEffect> effects = player.getActivePotionEffects();

        for (PotionEffect effect : effects) {
            player.removePotionEffect(effect.getType());
        }
    }

	/**
	 * Reset the players health.
	 */
    public void resetHealth() {
        player.setHealth(player.getMaxHealth());
    }

	/**
	 * Reset the players food.
	 */
    public void resetFood() {
        player.setSaturation(5.0F);
        player.setExhaustion(0F);
        player.setFoodLevel(20);
    }

	/**
	 * Reset the players xp.
	 */
    public void resetExp() {
        player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0F);
    }

	/**
	 * Reset the players inventory.
	 */
    public void resetInventory() {
    	final PlayerInventory inv = player.getInventory();

        inv.clear();
        inv.setArmorContents(null);
        player.setItemOnCursor(new ItemStack(Material.AIR));

        final InventoryView openInventory = player.getOpenInventory();
        
        if (openInventory.getType() == InventoryType.CRAFTING) {
            openInventory.getTopInventory().clear();
        }
    }
	
    /**
     * The ranking enum class.
     * 
     * @author LeonTG77
     */
    public enum Rank {
    	DEFAULT(1), DONATOR(2), SPEC(3), STAFF(4), TRIAL(5), HOST(6), OWNER(7);
    	
    	int level;
    	
    	private Rank(final int level) {
    		this.level = level;
    	}
    	
    	/**
    	 * Get the level of the rank.
    	 * <p>
    	 * It goes in order from 1 to 7 with 7 being the highest rank and 1 being the lowest.
    	 * 
    	 * @return The level.
    	 */
    	public int getLevel() {
    		return level;
    	}
    }
    
    /**
     * The stats enum class.
     * 
     * @author LeonTG77
     */
    public enum Stat {
    	WINS("Wins"), 
    	GAMESPLAYED("Games played"), 
    	KILLS("Kills"), 
    	DEATHS("Deaths"), 
    	DAMAGETAKEN("Damage taken"), 
    	ARENAKILLS("Arena Kills"), 
    	ARENADEATHS("Arena Deaths"), 
    	KILLSTREAK("Highest Killstreak"), 
    	ARENAKILLSTREAK("Highest Arena Killstreak"), 
    	GOLDENAPPLESEATEN("Golden Apples Eaten"),
    	GOLDENHEADSEATEN("Golden Heads Eaten"), 
    	HORSESTAMED("Horses Tamed"), 
    	WOLVESTAMED("Wolves Tamed"), 
    	POTIONS("Potions Drunk"), 
    	NETHER("Went to Nether"), 
    	END("Went to The End"), 
    	DIAMONDS("Mined diamonds"),
    	GOLD("Mined gold"),
    	HOSTILEMOBKILLS("Killed a monster"),
    	ANIMALKILLS("Killed an animal"),
    	LONGESTSHOT("Longest Shot"),
    	LEVELS("Levels Earned");
    	
    	private String name;
    	
    	private Stat(final String name) {
    		this.name = name;
    	}
    	
    	/**
    	 * Get the name of the stat.
    	 * 
    	 * @return The name.
    	 */
    	public String getName() {
    		return name;
    	}
    	
    	public Stat getStat(final String stat) {
    		try {
    			return valueOf(stat);
    		} catch (Exception e) {
    			for (Stat stats : values()) {
    				if (stats.getName().startsWith(stat)) {
    					return stats;
    				}
    			}
    		}
    		
    		return null;
    	}
    }
}