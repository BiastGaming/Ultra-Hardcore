package com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api;

import java.util.List;

import org.bukkit.plugin.Plugin;

/**
 * Extend this class for each version of Bukkit to support.
 *
 * Also requries implementing EntityChecker, EntityClassReplacer and providing a list of MobOverride
 */
public abstract class NMSHandler {

    protected final Plugin plugin;

    public NMSHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * @return NMS specific EntityChecker
     */
    public abstract EntityChecker getEntityChecker();

    /**
     * @return NMS specific EntityClassReplacer
     */
    public abstract EntityClassReplacer getEntityClassReplacer();

    /**
     * @return NMS specific MobOverride list
     */
    public abstract List<MobOverride> getMobOverrides();

    public abstract NewSpawnsModifier getNewSpawnsModifier();
}
