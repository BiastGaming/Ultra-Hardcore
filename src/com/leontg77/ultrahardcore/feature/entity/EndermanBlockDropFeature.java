package com.leontg77.ultrahardcore.feature.entity;

import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Enderman Block Drop feature class.
 * 
 * @author LeonTG77
 */
public class EndermanBlockDropFeature extends Feature implements Listener {

    /**
     * Enderman Block Drop feature class constructor.
     */
    public EndermanBlockDropFeature() {
        super("Enderman Block Drop", "Makes enderman drop the block their holding when they die.");
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Enderman)) {
            return;
        }

        Enderman ender = (Enderman) entity;
        event.getDrops().add(new ItemStack(ender.getCarriedMaterial().getItemType()));
    }
}