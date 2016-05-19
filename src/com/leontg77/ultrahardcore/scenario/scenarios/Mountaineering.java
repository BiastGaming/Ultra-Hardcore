package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Mountaineering scenario class.
 * 
 * @author LeonTG77
 */
public class Mountaineering extends Scenario {
    private static final ItemStack ENCHANT_TABLE = new ItemStack(Material.ENCHANTMENT_TABLE);

    private final Recipe emeraldEnch;
    private final Recipe diamondEnch;

    /**
     * Mountaineering scenario class constructor.
     */
    public Mountaineering() {
        super("Mountaineering", "Enchanting Tables are crafted with emeralds instead of diamonds.");

        emeraldEnch = new ShapedRecipe(ENCHANT_TABLE).shape(" B ", "EOE", "OOO")
                .setIngredient('B', Material.BOOK)
                .setIngredient('E', Material.EMERALD)
                .setIngredient('O', Material.OBSIDIAN
        );

        diamondEnch = new ShapedRecipe(ENCHANT_TABLE).shape(" B ", "DOD", "OOO")
                .setIngredient('B', Material.BOOK)
                .setIngredient('D', Material.DIAMOND)
                .setIngredient('O', Material.OBSIDIAN
        );
    }

    @Override
    public void onDisable() {
        removeEnchantRecipe();
        Bukkit.addRecipe(diamondEnch);
    }

    @Override
    public void onEnable() {
        removeEnchantRecipe();
        Bukkit.addRecipe(emeraldEnch);
    }

    /**
     * Remove the current enchantment table recipe.
     */
    private void removeEnchantRecipe() {
        Iterator<Recipe> it = Bukkit.recipeIterator();

        while (it.hasNext()) {
            Recipe res = it.next();

            if (res.getResult().getType() == ENCHANT_TABLE.getType()) {
                it.remove();
            }
        }
    }
}