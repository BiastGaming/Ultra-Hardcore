package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * GoldenAnvil scenario class.
 * 
 * @author LeonTG77
 */
public class GoldenAnvil extends Scenario {
    private static final ItemStack ANVIL = new ItemStack(Material.ANVIL);

    private final Recipe goldAnvil;
    private final Recipe ironAnvil;

    public GoldenAnvil() {
        super("GoldenAnvil", "Anvils are crafted with gold/gold blocks instead of iron/iron blocks.");

        goldAnvil = new ShapedRecipe(ANVIL).shape("BBB", " I ", "III")
                .setIngredient('B', Material.GOLD_BLOCK)
                .setIngredient('I', Material.GOLD_INGOT
        );

        ironAnvil = new ShapedRecipe(ANVIL).shape("BBB", " I ", "III")
                .setIngredient('B', Material.GOLD_BLOCK)
                .setIngredient('I', Material.GOLD_INGOT
        );
    }

    @Override
    public void onDisable() {
        removeAnvilRecipe();
        Bukkit.addRecipe(ironAnvil);
    }

    @Override
    public void onEnable() {
        removeAnvilRecipe();
        Bukkit.addRecipe(goldAnvil);
    }

    /**
     * Remove the current anvil recipe.
     */
    private void removeAnvilRecipe() {
        Iterator<Recipe> it = Bukkit.recipeIterator();

        while (it.hasNext()) {
            Recipe res = it.next();

            if (res.getResult().getType() == ANVIL.getType()) {
                it.remove();
            }
        }
    }
}