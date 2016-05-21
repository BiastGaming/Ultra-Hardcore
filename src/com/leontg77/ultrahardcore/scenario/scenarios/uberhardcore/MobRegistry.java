package com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.leontg77.ultrahardcore.scenario.scenarios.UberHardcore;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.EntityChecker;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.EntityClassReplacer;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.MobOverride;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.NewSpawnsModifier;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.PluginDependantListener;

public class MobRegistry {
    protected final UberHardcore uber;
    protected final Plugin plugin;

    protected final EntityClassReplacer replacer;
    protected final EntityChecker entityChecker;
    protected final NewSpawnsModifier newSpawnsModifier;
    protected final EntityKiller entityKiller;
    protected final List<MobOverride> overrides;

    public MobRegistry(Plugin plugin, UberHardcore uber, EntityClassReplacer replacer, EntityChecker entityChecker, NewSpawnsModifier newSpawnsModifier, EntityKiller entityKiller, List<MobOverride> overrides) {
        this.plugin = plugin;
        this.uber = uber;
        
        this.replacer = replacer;
        this.entityChecker = entityChecker;
        this.newSpawnsModifier = newSpawnsModifier;
        this.entityKiller = entityKiller;
        this.overrides = overrides;
    }

    /**
     * Register our custom entities. Kills any loaded entities of the original type
     */
    public void registerEntities() {
        for (MobOverride override : overrides) {
            if (override.isRunningClassReplace()) {
                // replace original classes
                replacer.replaceClasses(override.getNmsClass(), override.getOverrideClass());

                // add a listener for original class
                Bukkit.getPluginManager().registerEvents(new InvalidSpawnListener(plugin, uber, entityChecker, override.getNmsClass(), override.getSuppressedInvalidSpawnReasons()), plugin);
            }

            // register events for the override
            for (Listener listener : override.getListeners()) {
                if (listener instanceof PluginDependantListener) {
                    ((PluginDependantListener) listener).setPlugin(plugin);
                }

                Bukkit.getPluginManager().registerEvents(listener, plugin);
            }

            // kill all of the original classes
            for (World world : Bukkit.getWorlds()) {
                entityKiller.killEntitiesInWorld(override.getNmsClass(), world);
            }
        }

        newSpawnsModifier.setup();
    }

    /**
     * Should only be called in onDisable, does not cleanup listeners
     */
    public void deregisterEntities() {
        newSpawnsModifier.desetup();

        for (MobOverride override : overrides) {
            if (override.isRunningClassReplace()) {
                // reverse the replace
                replacer.replaceClasses(override.getOverrideClass(), override.getNmsClass());
            }
            
            for (Listener listener : override.getListeners()) {
                HandlerList.unregisterAll(listener);
            }

            // kill the replacement classes
            for (World world : Bukkit.getWorlds()) {
                entityKiller.killEntitiesInWorld(override.getOverrideClass(), world);
            }
        }
    }
}
