package com.leontg77.ultrahardcore.feature;

import org.bukkit.Bukkit;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;

/**
 * A feature of the plugin.
 * 
 * @author LeonTG77
 */
public abstract class Feature {
    private String description;
    private String name;

    /**
     * Feature class constructor
     *
     * @param name The name of the feature.
     * @param description A short description of the feature.
     */
    public Feature(String name, String description) {
        this.description = description;
        this.name = name;
    }

    protected Main plugin = (Main) Bukkit.getPluginManager().getPlugin("UHC");
    protected Game game;
    
    /**
     * Setup the 2 instances needed.
     * 
     * @param plugin The plugin instance.
     * @param game The game instance
     */
    protected void setupInstances(Main plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    /**
     * Get the name of the feature
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get a description of the feature.
     *
     * @return The description.
     */
    public String getDescription() {
        return description;
    }
}