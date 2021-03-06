package com.leontg77.ultrahardcore.feature.entity;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import com.leontg77.ultrahardcore.feature.ToggleableFeature;

/**
 * Merged feature base class.
 * 
 * @author D4mnX
 */
abstract class MergeFeatureBase extends ToggleableFeature implements Listener {

    public MergeFeatureBase(String name, String description) {
        super(name, description);
    }

    @Override
    public void onDisable() {
        Bukkit.getWorlds().forEach(this::updateWorld);
    }

    @Override
    public void onEnable() {
        Bukkit.getWorlds().forEach(this::updateWorld);
    }

    @EventHandler
    protected void on(WorldLoadEvent event) {
        updateWorld(event.getWorld());
    }

    protected abstract void updateWorld(World world);
}