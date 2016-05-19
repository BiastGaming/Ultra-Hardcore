package com.leontg77.ultrahardcore;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
 
/**
 * Settings class to manage all the config files.
 * <p>
 * This class contains methods for saving, getting and reloading the config, hof and data.yml!
 * 
 * @author LeonTG77
 */
public class Settings {
    private final Main plugin;
    
    public Settings(Main plugin) {
        this.plugin = plugin;
    }

    private FileConfiguration config;
    private File cfile;

    private FileConfiguration data;
    private File dfile;

    private FileConfiguration hof;
    private File hfile;

    private FileConfiguration swap;
    private File sfile;

    private FileConfiguration worlds;
    private File wfile;

    /**
     * Sets the settings configs and create missing files.
     */
    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        
        cfile = new File(plugin.getDataFolder(), "config.yml");
        dfile = new File(plugin.getDataFolder(), "data.yml");
        hfile = new File(plugin.getDataFolder(), "hof.yml");
        sfile = new File(plugin.getDataFolder(), "swap.yml");
        wfile = new File(plugin.getDataFolder(), "worlds.yml");

        if (!cfile.exists()) {
            try {
                cfile.createNewFile();
            } catch (Exception e) {
                plugin.getLogger().severe(ChatColor.RED + "Could not create config.yml!");
            }
        }

        if (!dfile.exists()) {
            try {
                dfile.createNewFile();
            } catch (Exception e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create data.yml!");
            }
        }
        
        if (!hfile.exists()) {
            try {
                hfile.createNewFile();
            } catch (Exception e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create hof.yml!");
            }
        }
               
        if (!sfile.exists()) {
            try {
                sfile.createNewFile();
            } catch (Exception e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create swap.yml!");
            }
        }
        
        if (!wfile.exists()) {
            try {
                wfile.createNewFile();
            } catch (Exception e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create worlds.yml!");
            }
        }

        config = YamlConfiguration.loadConfiguration(cfile);
        data = YamlConfiguration.loadConfiguration(dfile);
        hof = YamlConfiguration.loadConfiguration(hfile);
        swap = YamlConfiguration.loadConfiguration(sfile);
        worlds = YamlConfiguration.loadConfiguration(wfile);

        plugin.getLogger().info("Configs has been setup.");
    }
    
    /**
     * Gets the config file.
     *
     * @return The file.
     */
    public FileConfiguration getConfig() {
        return config;
    }
    
    /**
     * Saves the data config.
     */
    public void saveConfig() {
        try {
            config.save(cfile);
        } catch (Exception e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save config.yml!");
        }
    }
    
    /**
     * Reloads the config file.
     */
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(cfile);
    }
    
    /**
     * Gets the data file.
     *
     * @return The file.
     */
    public FileConfiguration getData() {
        return data;
    }
    
    /**
     * Saves the data config.
     */
    public void saveData() {
        try {
            data.save(dfile);
        } catch (Exception e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save data.yml!");
        }
    }
    
    /**
     * Reloads the data file.
     */
    public void reloadData() {
        data = YamlConfiguration.loadConfiguration(dfile);
    }
    
    /**
     * Gets the hof file.
     *
     * @return The file.
     */
    public FileConfiguration getHOF() {
        return hof;
    }
    
    /**
     * Saves the hof config.
     */
    public void saveHOF() {
        try {
            hof.save(hfile);
        } catch (Exception e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save hof.yml!");
        }
    }
    
    /**
     * Reloads the hof file.
     */
    public void reloadHOF() {
        hof = YamlConfiguration.loadConfiguration(hfile);
    }
    
    /**
     * Gets the swap file.
     *
     * @return The file.
     */
    public FileConfiguration getSwap() {
        return swap;
    }
    
    /**
     * Saves the swap config.
     */
    public void saveSwap() {
        try {
            swap.save(sfile);
        } catch (Exception e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save swap.yml!");
        }
    }
    
    /**
     * Reloads the swap file.
     */
    public void reloadSwap() {
        swap = YamlConfiguration.loadConfiguration(sfile);
    }
    
    /**
     * Gets the worlds file.
     *
     * @return The file.
     */
    public FileConfiguration getWorlds() {
        return worlds;
    }
    
    /**
     * Saves the worlds config.
     */
    public void saveWorlds() {
        try {
            worlds.save(wfile);
        } catch (Exception e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save worlds.yml!");
        }
    }
    
    /**
     * Reloads the worlds file.
     */
    public void reloadWorlds() {
        worlds = YamlConfiguration.loadConfiguration(wfile);
    }
}