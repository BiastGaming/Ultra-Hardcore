package com.leontg77.ultrahardcore.feature.potions;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Regen Potions potion feature class.
 * 
 * @author LeonTG77
 */
public class RegenPotionFeature extends Feature implements Listener {
    private static final Material FEATURE_ITEM = Material.GHAST_TEAR;

    public RegenPotionFeature(PotionFuelListener listener) {
        super("Regen Potions", "A potion that regenerates your health faster.");

        listener.addMaterial(FEATURE_ITEM, Main.PREFIX + "Regen potions are disabled.");
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        final Entity entity = event.getEntity();

        if (!(entity instanceof Ghast)) {
            return;
        }

        for (ItemStack drop : event.getDrops()) {
            if (!drop.getType().equals(FEATURE_ITEM)) {
                continue;
            }
            
            drop.setType(Material.GOLD_INGOT);
        }
    }
}