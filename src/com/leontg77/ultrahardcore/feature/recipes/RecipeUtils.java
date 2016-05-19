package com.leontg77.ultrahardcore.feature.recipes;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

/**
 * Recipe utitities class.
 * 
 * @author LeonTG77
 */
public class RecipeUtils {

    /**
     * Check if the recipe has the given material in it
     *
     * @param recipe the recipe to check
     * @param type the material to look for
     * @return true if found, false if not
     */
    public static boolean hasRecipeGotMaterial(Recipe recipe, Material type) {
        Collection<ItemStack> ingredients = null;

        if (recipe instanceof ShapedRecipe) {
            ingredients = ((ShapedRecipe) recipe).getIngredientMap().values();
        } else if(recipe instanceof ShapelessRecipe) {
            ingredients = ((ShapelessRecipe) recipe).getIngredientList();
        }

        if (null == ingredients) {
            return false;
        }

        for (ItemStack stack : ingredients) {
            if (stack.getType() == type) {
                return true;
            }
        }

        return false;
    }
}