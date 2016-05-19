package com.leontg77.ultrahardcore.feature.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Glistering Melon Recipe feature class.
 * 
 * @author LeonTG77
 */
public class GlisteringMelonRecipeFeature extends Feature implements Listener {

    public GlisteringMelonRecipeFeature() {
        super("Glistering Melon Recipe", "Makes glistering melons require gold ingots rather than nuggets to be crafted.");

        // register the new recipe
        final ShapedRecipe recipe = new ShapedRecipe(new ItemStack(Material.SPECKLED_MELON, 1))
            .shape("AAA", "ABA", "AAA")
            .setIngredient('A', Material.GOLD_INGOT)
            .setIngredient('B', Material.MELON);

        Bukkit.addRecipe(recipe);
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        final Recipe recipe = event.getRecipe();

        if (recipe.getResult().getType() != Material.SPECKLED_MELON) {
            return;
        }

        if (RecipeUtils.hasRecipeGotMaterial(recipe, Material.GOLD_NUGGET)) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }
}