package com.leontg77.ultrahardcore.feature.recipes;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import com.leontg77.ultrahardcore.feature.ToggleableFeature;

/**
 * Notch apples feature class.
 * 
 * @author LeonTG77
 */
public class NotchApplesFeature extends ToggleableFeature implements Listener {

    public NotchApplesFeature() {
        super("Notch Apples", "A powerful golden apple crafted with golden blocks.");

        icon.setType(Material.GOLDEN_APPLE);
        icon.setDurability((short) 1);

        slot = 2;
    }

    @EventHandler
    public void on(final PrepareItemCraftEvent event) {
        if (isEnabled()) {
            return;
        }

        final Recipe recipe = event.getRecipe();

        if (recipe.getResult().getType() != Material.GOLDEN_APPLE) {
            return;
        }

        if (recipe.getResult().getDurability() == 1) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }
}