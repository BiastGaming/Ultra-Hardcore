package com.leontg77.ultrahardcore.feature.potions;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * Tier 2 potion feature class.
 * 
 * @author LeonTG77
 */
public class Tier2PotionFeature extends ToggleableFeature implements Listener {
    private static final Material FEATURE_ITEM = Material.GLOWSTONE_DUST;

    private final PotionFuelListener listener;

    public Tier2PotionFeature(PotionFuelListener listener) {
        super("Tier 2", "Make potions be stronger by adding another tier to them.");

        this.listener = listener;

        icon.setType(FEATURE_ITEM);
        slot = 51;
    }

    @Override
    public void onDisable() {
        listener.addMaterial(FEATURE_ITEM, Main.PREFIX + "Tier 2 potions are disabled.");
    }

    @Override
    public void onEnable() {
        listener.removeMaterial(FEATURE_ITEM);
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (isEnabled()) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (block.getType() != Material.GLOWSTONE) {
            return;
        }

        BlockUtils.blockBreak(player, block);
        BlockUtils.degradeDurabiliy(player);
        BlockUtils.dropItem(block.getLocation(), new ItemStack(Material.GLOWSTONE));

        event.setCancelled(true);
        block.setType(Material.AIR);
    }
}