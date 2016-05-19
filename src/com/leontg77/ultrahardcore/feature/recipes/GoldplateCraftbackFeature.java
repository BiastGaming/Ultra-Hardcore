package com.leontg77.ultrahardcore.feature.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Goldplate Craftback feature class.
 * 
 * @author LeonTG77
 */
public class GoldplateCraftbackFeature extends Feature {

    public GoldplateCraftbackFeature() {
        super("Goldplate Craftback", "Allows you to craft the gold plate back to iron.");

        // register the new recipe
        final ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(Material.GOLD_INGOT, 2)).addIngredient(Material.GOLD_PLATE);
        
        Bukkit.addRecipe(recipe);
    }
}