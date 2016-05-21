package com.leontg77.ultrahardcore.feature.entity;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Pets feature class.
 *  
 * @author LeonTG77
 */
public class PetFeature extends Feature implements Listener {

    public PetFeature() {
        super("Pets", "Name wolves \"Wolf\" and cats \"Cat\" so they don't despawn and up their spawn rates.");
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(CreatureSpawnEvent event) {
        final Entity entity = event.getEntity();

        final Location loc = event.getLocation();
        final Biome biome = loc.getBlock().getBiome();

        switch (entity.getType()) {
        case WOLF:
            entity.setCustomName("Wolf");
            break;
        case OCELOT:
            entity.setCustomName("Cat");
            break;
        case RABBIT:
        case SHEEP:
            switch (biome) {
            case FOREST:
            case FOREST_HILLS:
                Wolf wolf = loc.getWorld().spawn(loc, Wolf.class);
                wolf.setCustomName("Wolf");

                event.setCancelled(true);
            case JUNGLE:
            case JUNGLE_EDGE:
            case JUNGLE_EDGE_MOUNTAINS:
            case JUNGLE_HILLS:
            case JUNGLE_MOUNTAINS:
                Ocelot cat = loc.getWorld().spawn(loc, Ocelot.class);
                cat.setCustomName("Cat");

                event.setCancelled(true);
                break;
            default:
                break;
            }
            break;
        default:
            break;
        }
    }
}