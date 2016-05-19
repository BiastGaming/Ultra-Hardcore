package com.leontg77.ultrahardcore.feature.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Ironplate Craftback feature class.
 * 
 * @author LeonTG77
 */
public class IronplateCraftbackFeature extends Feature {

    public IronplateCraftbackFeature() {
        super("Ironplate Craftback", "Allows you to craft the iron plate back to iron.");

        // register the new recipe
        final ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(Material.IRON_INGOT, 2)).addIngredient(Material.IRON_PLATE);
        
        Bukkit.addRecipe(recipe);
    }
}