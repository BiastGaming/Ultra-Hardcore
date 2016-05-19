package com.leontg77.ultrahardcore.feature.enchants;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;

/**
 * Anvils feature class.
 * 
 * @author LeonTG77
 */
public class AnvilsFeature extends ToggleableFeature implements Listener {

    public AnvilsFeature() {
        super("Anvils", "A block to repair, combine and rename items.");

        icon.setType(Material.ANVIL);
        slot = 46;
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        if (isEnabled()) {
            return;
        }

        final Recipe recipe = event.getRecipe();

        if (recipe.getResult().getType() == Material.ANVIL) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        if (isEnabled()) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (block.getType() == Material.ANVIL) {
            player.sendMessage(Main.PREFIX + "Anvils are disabled.");
            block.setType(Material.AIR);
        }
    }
}