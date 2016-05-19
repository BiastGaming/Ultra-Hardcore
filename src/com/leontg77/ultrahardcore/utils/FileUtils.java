package com.leontg77.ultrahardcore.utils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.leontg77.ultrahardcore.Main;

/**
 * File utilities class.
 * <p>
 * Contains file related methods.
 * 
 * @author LeonTG77
 */
public class FileUtils {
    private static Set<FileConfiguration> files = new HashSet<FileConfiguration>();

    /**
     * Get a list of all the user file configurations.
     *
     * @return The list of file configurations.
     */
    public static Set<FileConfiguration> getUserFiles() {
        return files;
    }

    /**
     * Update the files storage.
     */
    public static void updateUserFiles(Main plugin) {
        File folder = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);

        if (!folder.exists()) {
            return;
        }

        Bukkit.getLogger().info("Loading user files...");

        files.clear();

        for (File file : folder.listFiles()) {
            files.add(YamlConfiguration.loadConfiguration(file));
        }

        Bukkit.getLogger().info("User files loaded.");
    }

    /**
     * Delete all the minecraft playerdata and stats files.
     */
    public static void deletePlayerDataAndStats(Main plugin) {
        final File playerData = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata");
        final File stats = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "stats");

        int totalDatafiles = 0;
        int totalStatsfiles = 0;

        if (playerData.exists()) {
            for (File dataFiles : playerData.listFiles()) {
                try {
                    dataFiles.delete();
                    totalDatafiles++;
                } catch (Exception e) {
                    plugin.getLogger().warning("Could not delete " + dataFiles.getName() + ".");
                }
            }
        }

        if (stats.exists()) {
            for (File statsFiles : stats.listFiles()) {
                try {
                    statsFiles.delete();
                    totalStatsfiles++;
                } catch (Exception e) {
                    plugin.getLogger().warning("Could not delete " + statsFiles.getName() + ".");
                }
            }
        }

        plugin.getLogger().info("Deleted " + totalDatafiles + " player data files.");
        plugin.getLogger().info("Deleted " + totalStatsfiles + " player stats files.");
    }

    /**
     * Deletes the given file and it's subfiles.
     *
     * @return True if sucess, false otherwise.
     * @author D4mnX
     */
    public static boolean deleteFile(final File path) {
        if (!path.exists()) {
            return false;
        }

        final File files[] = path.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                deleteFile(files[i]);
            } else {
                files[i].delete();
            }
        }

        return path.delete();
    }
}