package com.leontg77.ultrahardcore.scenario;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.Parser;

/**
 * Scenario super class.
 * 
 * @author LeonTG77
 */
public abstract class Scenario extends Parser {
    private boolean enabled = false;

    private String description;
    private String name;

    /**
     * The scenario class constructor
     *
     * @param name The name of the scenario.
     * @param description The description of the scenario.
     */
    protected Scenario(String name, String description) {
        this.description = description;
        this.name = name;
    }

    protected Main plugin;
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
     * Get the name of the scenario
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get a description of the scenario.
     *
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Enable the scenario.
     *
     * @return True if successful, false otherwise.
     */
    public boolean enable(Main plugin) {
        if (isEnabled()) {
            return false;
        }

        if (this instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) this, plugin);
        }

        enabled = true;
        onEnable();
        return true;
    }

    /**
     * Disable the scenario.
     *
     * @return True if successful, false otherwise.
     */
    public boolean disable() {
        if (!isEnabled()) {
            return false;
        }

        if (this instanceof Listener) {
            HandlerList.unregisterAll((Listener) this);
        }

        enabled = false;
        onDisable();
        return true;
    }

    /**
     * Toggle the scenario.
     *
     * @return True if successful, false otherwise.
     */
    public boolean toggle(Main plugin) {
        return isEnabled() ? disable() : enable(plugin);
    }

    /**
     * Check if the scenario is enabled
     *
     * @return True if enabled, false otherwise.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Called when the scenario is disabled.
     */
    public void onDisable() {}

    /**
     * Called when the scenario is enabled.
     */
    public void onEnable() {}
}