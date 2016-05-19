package com.leontg77.ultrahardcore.feature.enchants;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
 * Bookshelf feature class.
 * 
 * @author LeonTG77
 */
public class BookshelfFeature extends ToggleableFeature implements Listener {

    public BookshelfFeature() {
        super("Bookshelves", "A block you surrond enchantment tables with to increase the level.");

        icon.setType(Material.BOOKSHELF);
        slot = 45;
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        if (isEnabled()) {
            return;
        }

        final Recipe recipe = event.getRecipe();

        if (recipe.getResult().getType() == Material.BOOKSHELF) {
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

        switch (block.getType()) {
        case BOOKSHELF:
            if (searchForNearby(block, Material.ENCHANTMENT_TABLE) || searchForNearby(block.getRelative(BlockFace.DOWN), Material.ENCHANTMENT_TABLE)) {
                player.sendMessage(Main.PREFIX + "There is an enchantment table nearby and bookshelves are disabled.");
                event.setCancelled(true);
            }
            break;
        case ENCHANTMENT_TABLE:
            if (searchForNearby(block, Material.BOOKSHELF) || searchForNearby(block.getRelative(BlockFace.UP), Material.BOOKSHELF)) {
                player.sendMessage(Main.PREFIX + "There is a bookshelf nearby and bookshelves are disabled.");
                event.setCancelled(true);
            }
            break;
        default:
            return;
        }
    }

    /**
     * Check if the given material type is nearby the given block.
     *
     * @param block The block to check nearby.
     * @param type The type to check for.
     * @return True if found, false otherwise.
     */
    private boolean searchForNearby(Block block, Material type) {
        for (BlockFace face : BlockFace.values()) {
            for (int i = 1; i <= 3; i++) {
                final Block newBlock = block.getRelative(face, i);

                if (newBlock.getType().equals(type)) {
                    return true;
                }
            }
        }

        return false;
    }
}