package com.leontg77.ultrahardcore;
 
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.gui.InvGUI;
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
	private static Main plugin;
	
	private static InvGUI gui;
	private static Game game;
	
	private static PermissionsManager perm;
	
	/**
	 * Setup the instances.
	 * 
	 * @param plugins The main class.
	 * @param games The game class
	 * @param guis The gui class.
	 */
	public User(Main plugins, Game games, InvGUI guis, PermissionsManager perms) {
		plugin = plugins;
		
		game = games;
		gui = guis;
		
		perm = perms;
	}
	
	public static final File FOLDER = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);
	
	private Player player;
	private String uuid;
	
	private FileConfiguration config;
	private File file;
    
    private boolean creating = false;
    
    private Location deathLoc;
    private Location lastLoc;
	
	/**
	 * Gets the data of the given player.
	 * <p>
	 * If the data doesn't exist it will create a new data file and threat the player as a newly joined one.
	 * 
	 * @param player the player.
	 * @return the data instance for the player.
	 */
	public static User get(Player player) {
		return new User(player, player.getUniqueId().toString());
	}

	/**
	 * Gets the data of the given OFFLINE player.
	 * <p>
	 * If the data doesn't exist it will create a new data file and threat the player as a newly joined one.
	 * 
	 * @param offline the offline player.
	 * @return the data instance for the player.
	 * 
	 * @throws CommandException If the offline player has never joined this server.
	 */
	public static User get(OfflinePlayer offline) throws CommandException {
		if (!fileExist(offline.getUniqueId())) {
			throw new CommandException("'" + offline.getName() + "' has never joined this server.");
		}
		
		return new User(offline.getPlayer(), offline.getUniqueId().toString());
	}
	
	/**
	 * Check if the userdata folder has a file with the given uuid.
	 * 
	 * @param uuid The uuid checking for.
	 * @return True if it exist, false otherwise.
	 */
	public static boolean fileExist(UUID uuid) {
		if (!FOLDER.exists() || !FOLDER.isDirectory()) {
			return false;
        }
		
		for (File file : FOLDER.listFiles()) {
			String fileName = file.getName().substring(0, file.getName().length() - 4);
			
			if (fileName.equals(uuid.toString())) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Constuctor for player data.
	 * <p>
	 * This will set up the data for the player and create missing data.
	 * 
	 * @param uuid the player.
	 * @param uuid the uuid of the player.
	 */
	private User(Player player, String uuid) {
        if (!plugin.getDataFolder().exists()) {
        	plugin.getDataFolder().mkdir();
        }
        
        if (!FOLDER.exists()) {
        	FOLDER.mkdir(); 
        }
        
        file = new File(FOLDER, uuid + ".yml");
        
        if (!file.exists()) {
        	try {
        		file.createNewFile();
        		creating = true;
        	} catch (Exception e) {
        		plugin.getLogger().severe(ChatColor.RED + "Could not create " + uuid + ".yml!");
        	}
        }
               
        config = YamlConfiguration.loadConfiguration(file);
        
        this.player = player;
        this.uuid = uuid;
        
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
    		FileUtils.getUserFiles().add(config);
        }
	}
	
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
	public void setRank(final Rank rank) {
		config.set("rank", rank.name());
		saveFile();
		
		FileUtils.updateUserFiles(plugin);
		gui.getGameInfo().updateStaff();
		
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
	 * Set the death location of the player.
	 * 
	 * @param location The death loc.
	 */
	public void setDeathLocation(final Location location) {
		deathLoc = location;
	}
	
	/**
	 * Get the death location of the player.
	 * 
	 * @return The death location.
	 */
	public Location getDeathLocation() {
		return deathLoc;
	}
	
	/**
	 * Set the death location of the player.
	 * 
	 * @param location The death loc.
	 */
	public void setLastLocation(final Location location) {
		lastLoc = location;
	}
	
	/**
	 * Get the death location of the player.
	 * 
	 * @return The death location.
	 */
	public Location getLastLocation() {
		return lastLoc;
	}
	
	private static final String IGNORE_PATH = "ignoreList";
	
	/**
	 * Start ignoring the given player.
	 * 
	 * @param player The player to ignore
	 */
	public void ignore(final Player player) {
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
	public void unIgnore(final Player player) {
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
	public boolean isIgnoring(final Player player) {
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
	public void mute(final String reason, final Date unmute) {
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
		return config.getBoolean("muted.status", false);
	}
	
	/**
	 * Get the reason the player is muted.
	 * 
	 * @return The reason of the mute, null if not muted.
	 */
	public String getMutedReason() {
		if (!isMuted()) {
			return "NOT_MUTED";
		}
		
		return config.getString("muted.reason", "NOT_MUTED");
	}
	
	/**
	 * Get the time in milliseconds for the unmute.
	 * 
	 * @return The unmute time.
	 */
	public Date getMuteExpiration() {
		if (!isMuted()) {
			return null;
		}
	
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