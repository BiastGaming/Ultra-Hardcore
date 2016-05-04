package com.leontg77.ultrahardcore.managers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Rank;

/**
 * Hall of fame manager class.
 * 
 * @author LeonTG77
 */
public class HOFManager implements Listener {
	private final Main plugin;
	private final File folder;

	/**
	 * Staff log manager class constructor.
	 * 
	 * @param plugin The main class.
	 */
	public HOFManager(Main plugin) {
		this.folder = new File(plugin.getDataFolder() + File.separator + "staff logs" + File.separator);
		
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	/**
	 * Get the file configuration for the given name's log.
	 * 
	 * @param name The name to use.
	 * @return The file configuration, newly created if needed.
	 */
	public FileConfiguration getLog(String name) {
		if (!folder.exists()) {
			folder.mkdir();
		}

		File file = new File(folder, name + ".yml");

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				plugin.getLogger().severe(ChatColor.RED + "Could not create " + name + ".yml!");
			}
		}

		return YamlConfiguration.loadConfiguration(file);
	}

	/**
	 * Save the given configuration to the file with the given name.
	 * 
	 * @param config The config to save.
	 * @param name The name of the file to save.
	 */
	public void saveLog(FileConfiguration config, String name) {
		try {
			config.save(new File(folder, name + ".yml"));
		} catch (Exception e) {
			plugin.getLogger().severe(ChatColor.RED + "Could not save " + name + ".yml!");
		}
	}

	private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy',' HH:mm:ss", Locale.US); 

	@EventHandler
	public void on(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();	
		User user = plugin.getUser(player);
		
		if (user.getRank().getLevel() < Rank.STAFF.getLevel()) {
			return;
		}
		
		List<String> list = getLog(player.getName()).getStringList("chat");
		list.add("(" + format.format(new Date()) + ") " + event.getMessage());
		
		FileConfiguration config = getLog(player.getName());
		
		config.set("chat", list);
		saveLog(config, player.getName());
	}
	
	@EventHandler
	public void on(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();	
		User user = plugin.getUser(player);
		
		if (user.getRank().getLevel() < Rank.STAFF.getLevel()) {
			return;
		}
		
		List<String> list = getLog(player.getName()).getStringList("commands");
		list.add("(" + format.format(new Date()) + ") " + event.getMessage());

		FileConfiguration config = getLog(player.getName());
		
		config.set("commands", list);
		saveLog(config, player.getName());
	}
}