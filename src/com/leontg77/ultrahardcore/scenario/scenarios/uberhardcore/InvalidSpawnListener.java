package com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.ImmutableSet;
import com.leontg77.ultrahardcore.scenario.scenarios.UberHardcore;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.EntityChecker;

public class InvalidSpawnListener implements Listener {
    protected final UberHardcore uber;
    protected final Plugin plugin;

    protected final Class<?> toCheck;
    protected final EntityChecker entityChecker;
    protected final Set<CreatureSpawnEvent.SpawnReason> skips;

    public InvalidSpawnListener(Plugin plugin, UberHardcore uber, EntityChecker entityChecker, Class<?> toCheck, Set<CreatureSpawnEvent.SpawnReason> skips) {
        this.plugin = plugin;
        this.uber = uber;
        
        this.toCheck = toCheck;
        this.entityChecker = entityChecker;
        this.skips = ImmutableSet.copyOf(skips);
    }

    @EventHandler
    public void on(CreatureSpawnEvent event) {
        if (!uber.isEnabled()) {
            HandlerList.unregisterAll(this);
            return;
        }

        if (!entityChecker.isEntityOfClassExact(event.getEntity(), toCheck)) {
            return;
        }

        if (skips.contains(event.getSpawnReason())) {
            return;
        }

        Location loc = event.getEntity().getLocation();
        plugin.getLogger().severe(
                String.format("Invalid spawn occured for entity type %s at x:%f y:%f z:%f, automatically cancelling...",
                        toCheck.getName(),
                        loc.getX(),
                        loc.getY(),
                        loc.getZ()
                )
        );

        event.setCancelled(true);
    }
}
